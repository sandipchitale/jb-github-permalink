package dev.sandipchitale.productivity.plugins.githubpermalink;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Service providing the logic to build a GitHub permalink for a file at its last commit and
 * either OPEN it in the browser or COPY it to clipboard. If line is not provided, assumes line 1.
 */
public final class GitHubPermalinkService {

    public enum ACTION {
        COPY,
        OPEN
    }

    private GitHubPermalinkService() {}

    /**
     * Performs the requested action (COPY or OPEN) for the GitHub commit-based permalink of the given file.
     * If line is null or invalid, uses line 1.
     */
    public static void doit(Project project, VirtualFile file, int line, ACTION action) {
        if (project == null || file == null || action == null) return;

        GitRepository gitRepository;
        try {
            gitRepository = GitUtil.getRepositoryForFile(project, file);
        } catch (VcsException e) {
            return;
        }

        String rootPath = gitRepository.getRoot().getPath();
        String filePath = file.getPath();
        if (!filePath.startsWith(rootPath)) return;
        String relativePath = filePath.substring(rootPath.length() + 1);

        GitLineHandler handler = new GitLineHandler(project, gitRepository.getRoot(), GitCommand.LOG);
        handler.addParameters("-1", "--pretty=format:%H", filePath);
        List<String> gitLogOutput = Git.getInstance().runCommand(handler).getOutput();
        if (gitLogOutput.isEmpty()) return;
        String commit = gitLogOutput.get(0);

        Collection<GitRemote> remotes = gitRepository.getRemotes();
        for (GitRemote remote : remotes) {
            String firstRemoteUrl = remote.getFirstUrl();
            if (firstRemoteUrl == null) continue;
            String url;
            if (firstRemoteUrl.startsWith("git@")) {
                firstRemoteUrl = firstRemoteUrl.replace("git@", "").replace(".git", "").replace(':', '/');
                url = String.format("https://%s/blob/%s/%s#L%d", firstRemoteUrl, commit, relativePath, line);
            } else {
                firstRemoteUrl = firstRemoteUrl.replace(".git", "");
                url = String.format("%s/blob/%s/%s#L%d", firstRemoteUrl, commit, relativePath, line);
            }

            try {
                switch (action) {
                    case OPEN -> java.awt.Desktop.getDesktop().browse(new URI(url));
                    case COPY -> CopyPasteManager.getInstance().setContents(new StringSelection(url));
                }
            } catch (Exception ignore) {
            }
            break; // only first remote
        }
    }
}
