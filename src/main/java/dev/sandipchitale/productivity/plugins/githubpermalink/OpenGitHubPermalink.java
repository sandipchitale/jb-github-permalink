package dev.sandipchitale.productivity.plugins.githubpermalink;

import com.intellij.ide.SelectInContext;
import com.intellij.ide.SelectInTarget;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vcs.VcsException;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

public class OpenGitHubPermalink implements SelectInTarget {
    @Override
    public boolean canSelect(SelectInContext context) {
        VirtualFile vf = context.getVirtualFile();
        if (vf == null || !vf.isInLocalFileSystem()) return false;
        Project project = context.getProject();
        if (project == null) return false;
        try {
            GitRepository repo = GitUtil.getRepositoryForFile(project, vf);
            return repo != null;
        } catch (VcsException e) {
            return false;
        }
    }

    @Override
    public void selectIn(SelectInContext context, boolean requestFocus) {
        Project project = context.getProject();
        VirtualFile file = context.getVirtualFile();
        if (project == null || file == null) return;

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Open on GitHub", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitHubPermalinkService.doit(project, file, 1, GitHubPermalinkService.ACTION.OPEN);
            }
        });
    }

    @Override
    public float getWeight() {
        return 2000;
    }

    @Override
    public String toString() {
        return "Open on GitHub...";
    }
}
