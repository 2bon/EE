package net.dataninja.ee.textEngine;


/**
 * Copyright 2004 The Apache Software Foundation
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.PriorityQueue;

class HitQueue extends PriorityQueue 
{
  HitQueue(int size) {
    initialize(size);
  }

  protected final boolean lessThan(Object a, Object b) {
    ScoreDoc hitA = (ScoreDoc)a;
    ScoreDoc hitB = (ScoreDoc)b;
    if (hitA.score == hitB.score)
      return hitA.doc > hitB.doc;
    else
      return hitA.score < hitB.score;
  }
}