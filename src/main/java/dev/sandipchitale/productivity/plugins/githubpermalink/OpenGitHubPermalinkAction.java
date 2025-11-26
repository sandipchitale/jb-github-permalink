package dev.sandipchitale.productivity.plugins.githubpermalink;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class OpenGitHubPermalinkAction extends AbstractGitHubPermalinkAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        doIt(e, ACTION.OPEN);
    }
}
