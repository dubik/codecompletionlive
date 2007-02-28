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

import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.LookupData;
import com.intellij.codeInsight.completion.actions.ClassNameCompletionAction;
import com.intellij.codeInsight.completion.actions.CodeCompletionAction;
import com.intellij.codeInsight.completion.actions.SmartCodeCompletionAction;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.dubik.codecompletionlive.completions.CodeCompletionInvoker;
import org.dubik.codecompletionlive.completions.ILookupDataListener;
import org.dubik.codecompletionlive.completions.SmartCodeCompletionInvoker;

/**
 * @author Sergiy Dubovik
 */
public class CodeCompletionLiveActionExecutor implements ILookupDataListener {
    private Project project;
    private Editor editor;
    private PsiFile psiFile;
    private AnActionEvent actionEvent;

    private static CodeCompletionInvoker codeCompletionInvoker = new CodeCompletionInvoker();
    private static SmartCodeCompletionInvoker smartCodeCompletionInvoker = new SmartCodeCompletionInvoker();

    private CompletionContext context;

    private CompletionActionStates state = CompletionActionStates.ST_START;

    public CodeCompletionLiveActionExecutor(Project project, Editor editor,
                                            PsiFile psiFile, AnActionEvent actionEvent) {
        this.project = project;
        this.editor = editor;
        this.psiFile = psiFile;
        this.actionEvent = actionEvent;

        context = createCompletionContext();
    }

    public void execute() {
        if (state == CompletionActionStates.ST_FINNISH)
            state = CompletionActionStates.ST_START;

        doState();
    }

    private void doState() {
        switch (state) {
            case ST_START:
                state = CompletionActionStates.ST_SMART_COMPLETION;
                doState();
                break;

            case ST_SMART_COMPLETION:
                smartCodeCompletionInvoker.getCompletions(context, this);
                break;

            case ST_BASIC_COMPLETION:
                codeCompletionInvoker.getCompletions(context, this);
                break;

            case ST_CLASS_COMPLETION:
                state = CompletionActionStates.ST_FINNISH;
                AnAction codeCompletionAction = new ClassNameCompletionAction();
                performAction(codeCompletionAction);
                break;

            case ST_FINNISH:
                break;
        }
    }

    public void acceptLookupData(LookupData lookupData) {
        if (lookupData.items.length == 0) {
            if (state == CompletionActionStates.ST_SMART_COMPLETION) {
                state = CompletionActionStates.ST_BASIC_COMPLETION;
                doState();
            } else if (state == CompletionActionStates.ST_BASIC_COMPLETION) {
                state = CompletionActionStates.ST_CLASS_COMPLETION;
                doState();
            }
        } else {
            if (state == CompletionActionStates.ST_SMART_COMPLETION) {
                AnAction codeCompletionAction = new SmartCodeCompletionAction();
                performAction(codeCompletionAction);
                state = CompletionActionStates.ST_FINNISH;
                doState();
            } else if (state == CompletionActionStates.ST_BASIC_COMPLETION) {
                AnAction codeCompletionAction = new CodeCompletionAction();
                performAction(codeCompletionAction);
                state = CompletionActionStates.ST_FINNISH;
                doState();
            }
        }
    }

    private void performAction(AnAction action) {
        AnActionEvent event = new AnActionEvent(actionEvent.getInputEvent(),
                DataManager.getInstance().getDataContext(), actionEvent.getPlace(),
                actionEvent.getPresentation(), actionEvent.getActionManager(),
                actionEvent.getModifiers());
        action.actionPerformed(event);
    }

    /**
     * Creates <code>CompletionContext</code>.
     *
     * @return instance of <code>CompletionContext</code>
     */
    private CompletionContext createCompletionContext() {
        int i = editor.getSelectionModel().hasSelection() ? editor.getSelectionModel().getSelectionStart()
                : editor.getCaretModel().getOffset();
        int j = editor.getSelectionModel().hasSelection() ? editor.getSelectionModel().getSelectionEnd() : i;
        return new CompletionContext(project, editor, psiFile, i, j);
    }
}
