/*
 * (c) Copyright 2020 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.javaformat.java.java19;

import com.palantir.javaformat.OpsBuilder;
import com.palantir.javaformat.java.JavaInputAstVisitor;
import com.palantir.javaformat.java.java14.Java14InputAstVisitor;
import com.sun.source.tree.ConstantCaseLabelTree;
import com.sun.source.tree.DeconstructionPatternTree;
import com.sun.source.tree.ParenthesizedPatternTree;
import com.sun.source.tree.PatternCaseLabelTree;
import com.sun.source.tree.Tree;

/**
 * Extends {@link JavaInputAstVisitor} with support for AST nodes that were added or modified for Java 14.
 */
public class Java19InputAstVisitor extends Java14InputAstVisitor {

    public Java19InputAstVisitor(OpsBuilder builder, int indentMultiplier) {
        super(builder, indentMultiplier);
    }

    @Override
    public Void visitParenthesizedPattern(ParenthesizedPatternTree node, Void unused) {
        token("(");
        Void v = scan(node.getPattern(), unused);
        token(")");
        return v;
    }

    private Void scanAndReduceVoid(Iterable<? extends Tree> nodes, Void p, Void r) {
        return reduce(scan(nodes, p), r);
    }

    private Void scanAndReduceVoid(Tree node, Void p, Void r) {
        return reduce(scan(node, p), r);
    }

    @Override
    public Void visitDeconstructionPattern(DeconstructionPatternTree node, Void unused) {

        Void r = scan(node.getDeconstructor(), unused);
        token("(");
        r = scanAndReduceVoid(node.getNestedPatterns(), unused, r);
        r = scanAndReduceVoid(node.getVariable(), unused, r);
        token(")");
        return r;
    }

    @Override
    public Void visitConstantCaseLabel(ConstantCaseLabelTree node, Void unused) {
        return scan(node.getConstantExpression(), unused);
    }

    @Override
    public Void visitPatternCaseLabel(PatternCaseLabelTree node, Void unused) {
        Void r = scan(node.getPattern(), unused);
        if (node.getGuard() != null) {
            builder.space();
            token("when");
            builder.space();
            r = scanAndReduceVoid(node.getGuard(), unused, r);
        }
        return r;
    }
}
