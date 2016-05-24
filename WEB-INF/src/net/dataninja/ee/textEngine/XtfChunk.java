package net.dataninja.ee.textEngine;

import org.apache.lucene.chunk.Chunk;
import org.apache.lucene.chunk.ChunkSource;

/*
net.dataninja copyright statement
 */

/*
 * This file created on Jan 15, 2005 by Rick Li
 */

/** Keeps track of the tokens for a chunk, plus node and word offsets */
class XtfChunk extends Chunk 
{
  XtfChunk(ChunkSource source, int chunkNum) {
    super(source, chunkNum);
  }

  int startNodeNumber;
  int startWordOffset;
  String sectionType;
  int[] nodeNumbers;
  int[] wordOffsets;
}
