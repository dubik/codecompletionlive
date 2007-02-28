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
package org.dubik.codecompletionlive.completions;

import com.intellij.psi.PsiDocumentManager;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;

/**
 * @author Sergiy Dubovik
 */
public class SmartCodeCompletionInvoker extends com.intellij.codeInsight.completion.SmartCodeCompletionHandler
        implements ICodeCompletionInvoker {
    public SmartCodeCompletionInvoker() {
    }

    public void getCompletions(final CompletionContext context,
                               final ILookupDataListener listener) {
        CommandProcessor.getInstance().executeCommand(context.project, new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        PsiDocumentManager.getInstance(context.project).commitAllDocuments();
                        listener.acceptLookupData(SmartCodeCompletionInvoker.this.getLookupData(context));
                    }
                });
            }
        }, null, null);
    }
}
