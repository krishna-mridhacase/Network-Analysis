/*
 * (C) Copyright 2020-2023, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.interfaces;

import java.util.Map;

/**
 * An interface for all algorithms which assign scores to edges of a graph.
 * 
 * @param <E> the edge type
 * @param <D> the score type
 * 
 * @author Dimitrios Michail
 */
public interface EdgeScoringAlgorithm<E, D>
{

    /**
     * Get a map with the scores of all edges
     * 
     * @return a map with all scores
     */
    Map<E, D> getScores();

    /**
     * Get an edge score
     * 
     * @param e the edge
     * @return the score
     */
    D getEdgeScore(E e);

}
