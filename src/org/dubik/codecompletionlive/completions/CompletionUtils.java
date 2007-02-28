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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Document;

import static java.lang.Character.isJavaIdentifierPart;

/**
 * @author Sergiy Dubovik
 */
public class CompletionUtils {
    /**
     * Searches for a prefix in specified editor at caret position.
     * Caret position is taken from editor. Prefix contains letter
     * accepted by <code>Character.isJavaIdentifierPart</code> only.
     *
     * @param editor non null editor reference
     * @return found prefix
     */
    static public String findPrefix(Editor editor) {
        Document document = editor.getDocument();
        int caretOffset = editor.getCaretModel().getOffset();
        int currentOffset = caretOffset - 1;
        String text = document.getText();

        // move current caret to beggining of prefix
        while (currentOffset >= 0 && isJavaIdentifierPart(text.charAt(currentOffset)))
            --currentOffset;


        return text.substring(currentOffset + 1, caretOffset);
    }
}
