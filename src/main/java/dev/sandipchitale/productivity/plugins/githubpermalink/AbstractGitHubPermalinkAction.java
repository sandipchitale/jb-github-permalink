package dev.sandipchitale.productivity.plugins.githubpermalink;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.ex.EditorGutterComponentEx;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGitHubPermalinkAction extends AnAction {
    protected enum ACTION {
        COPY,
        OPEN
    }

    protected void doIt(@NotNull AnActionEvent anActionEvent, ACTION action) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            Integer lineGutterLine = anActionEvent.getDataContext().getData(EditorGutterComponentEx.LOGICAL_LINE_AT_CURSOR);

            VirtualFile editorVirtualFile = editor.getVirtualFile();
            VisualPosition selectionEndPosition = editor.getCaretModel().getCurrentCaret().getSelectionEndPosition();
            int endLine = (lineGutterLine == null ? editor.visualToLogicalPosition(selectionEndPosition).line : lineGutterLine) + 1;
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Copy or Open github permalink", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    GitHubPermalinkService.doit(
                            project,
                            editorVirtualFile,
                            endLine,
                            GitHubPermalinkService.ACTION.valueOf(action.name())
                    );
                }
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent actionEvent) {
        boolean enabledAndVisible = false;
        Project project = actionEvent.getProject();
        Editor editor = actionEvent.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            enabledAndVisible = true;
        }
        actionEvent.getPresentation().setEnabledAndVisible(enabledAndVisible);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
