package dev.sandipchitale.productivity.plugins.githubpermalink;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CopyGitHubPermalinkAction extends AbstractGitHubPermalinkAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        doIt(e, ACTION.COPY);
    }
}
