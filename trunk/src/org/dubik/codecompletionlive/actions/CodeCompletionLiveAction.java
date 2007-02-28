/*
 * Copyright 2006 Sergiy Dubovik
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dubik.codecompletionlive.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * @author Sergiy Dubovik
 */
public class CodeCompletionLiveAction extends AnAction {
    public void actionPerformed(final AnActionEvent event) {
        final Project project = (Project) event.getDataContext().getData(DataConstants.PROJECT);
        if (project == null)
            return;

        final PsiFile psiFile = (PsiFile) event.getDataContext().getData(DataConstants.PSI_FILE);
        if (psiFile == null)
            return;

        final Editor editor = (Editor) event.getDataContext().getData(DataConstants.EDITOR);
        if (editor == null)
            return;

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                CodeCompletionLiveActionExecutor executor =
                        new CodeCompletionLiveActionExecutor(project, editor, psiFile, event);
                executor.execute();
            }
        });
    }
}
