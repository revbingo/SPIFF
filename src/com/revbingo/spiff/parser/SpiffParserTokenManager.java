/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
/* Generated By:JavaCC: Do not edit this line. SpiffParserTokenManager.java */
package com.revbingo.spiff.parser;
import com.revbingo.spiff.instructions.*;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.parser.InstructionParser;
import java.util.*;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/** Token Manager. */
public class SpiffParserTokenManager implements SpiffParserConstants
{

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0xfff8L) != 0L)
         {
            jjmatchedKind = 52;
            return 78;
         }
         return -1;
      case 1:
         if ((active0 & 0xfff8L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 1;
            return 78;
         }
         return -1;
      case 2:
         if ((active0 & 0xffe8L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 2;
            return 78;
         }
         if ((active0 & 0x10L) != 0L)
            return 78;
         return -1;
      case 3:
         if ((active0 & 0x2f88L) != 0L)
         {
            if (jjmatchedPos != 3)
            {
               jjmatchedKind = 52;
               jjmatchedPos = 3;
            }
            return 78;
         }
         if ((active0 & 0xd060L) != 0L)
            return 78;
         return -1;
      case 4:
         if ((active0 & 0x908L) != 0L)
         {
            jjmatchedKind = 52;
            jjmatchedPos = 4;
            return 78;
         }
         if ((active0 & 0xa680L) != 0L)
            return 78;
         return -1;
      case 5:
         if ((active0 & 0x908L) != 0L)
            return 78;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_0(0x40000000000L);
      case 39:
         return jjStopAtPos(0, 25);
      case 40:
         return jjStopAtPos(0, 20);
      case 41:
         return jjStopAtPos(0, 21);
      case 44:
         return jjStopAtPos(0, 24);
      case 46:
         return jjMoveStringLiteralDfa1_0(0x7ffc000000L);
      case 60:
         jjmatchedKind = 41;
         return jjMoveStringLiteralDfa1_0(0x100000000000L);
      case 61:
         return jjMoveStringLiteralDfa1_0(0x8000000000L);
      case 62:
         jjmatchedKind = 40;
         return jjMoveStringLiteralDfa1_0(0x80000000000L);
      case 98:
         return jjMoveStringLiteralDfa1_0(0xc040L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x200L);
      case 105:
         return jjMoveStringLiteralDfa1_0(0x10L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x20L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x88L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x3c00L);
      case 123:
         return jjStopAtPos(0, 22);
      case 125:
         return jjStopAtPos(0, 23);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x8000000000L) != 0L)
            return jjStopAtPos(1, 39);
         else if ((active0 & 0x40000000000L) != 0L)
            return jjStopAtPos(1, 42);
         else if ((active0 & 0x80000000000L) != 0L)
            return jjStopAtPos(1, 43);
         else if ((active0 & 0x100000000000L) != 0L)
            return jjStopAtPos(1, 44);
         break;
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x400L);
      case 100:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000000000L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x400000000L);
      case 103:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x80L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x4080005000L);
      case 106:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x2200L);
      case 109:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x10L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x120L);
      case 112:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000000L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0x1148000800L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x8L);
      case 121:
         return jjMoveStringLiteralDfa2_0(active0, 0x8040L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000000L);
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x3118000000L);
      case 102:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(2, 31);
         break;
      case 104:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      case 107:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x400000000L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000001020L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x2280L);
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x220000008L);
      case 116:
         if ((active0 & 0x10L) != 0L)
            return jjStartNfaWithStates_0(2, 4, 78);
         return jjMoveStringLiteralDfa3_0(active0, 0xc040L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000100L);
      case 121:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x200L);
      case 98:
         return jjMoveStringLiteralDfa4_0(active0, 0x100L);
      case 99:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000000L);
      case 101:
         if ((active0 & 0x40L) != 0L)
         {
            jjmatchedKind = 6;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
      case 102:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000000000L);
      case 103:
         if ((active0 & 0x20L) != 0L)
            return jjStartNfaWithStates_0(3, 5, 78);
         break;
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x240000008L);
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x2000L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x20000800L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x800000080L);
      case 115:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(3, 14, 78);
         return jjMoveStringLiteralDfa4_0(active0, 0x400000000L);
      case 116:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(3, 12, 78);
         else if ((active0 & 0x100000000L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0x1008000400L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(4, 10, 78);
         else if ((active0 & 0x400000000L) != 0L)
            return jjStopAtPos(4, 34);
         return jjMoveStringLiteralDfa5_0(active0, 0x1010000000L);
      case 103:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(4, 13, 78);
         break;
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x2000000000L);
      case 107:
         if ((active0 & 0x800000000L) != 0L)
            return jjStopAtPos(4, 35);
         break;
      case 108:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000100L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x200000008L);
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
      case 112:
         if ((active0 & 0x4000000L) != 0L)
            return jjStopAtPos(4, 26);
         else if ((active0 & 0x40000000L) != 0L)
            return jjStopAtPos(4, 30);
         break;
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      case 115:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(4, 15, 78);
         break;
      case 116:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(4, 7, 78);
         else if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(4, 9, 78);
         break;
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x10000000L);
      case 101:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(5, 8, 78);
         break;
      case 103:
         if ((active0 & 0x8L) != 0L)
            return jjStartNfaWithStates_0(5, 3, 78);
         break;
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x3000000000L);
      case 112:
         if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(5, 29);
         break;
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000000L);
      case 116:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 78);
         else if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(5, 33);
         break;
      case 117:
         return jjMoveStringLiteralDfa6_0(active0, 0x4000000000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000000000L);
      case 100:
         return jjMoveStringLiteralDfa7_0(active0, 0x4008000000L);
      case 101:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(6, 37);
         break;
      case 116:
         if ((active0 & 0x10000000L) != 0L)
            return jjStopAtPos(6, 28);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x4000000000L) != 0L)
            return jjStopAtPos(7, 38);
         return jjMoveStringLiteralDfa8_0(active0, 0x8000000L);
      case 111:
         return jjMoveStringLiteralDfa8_0(active0, 0x1000000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa9_0(active0, 0x1000000000L);
      case 114:
         if ((active0 & 0x8000000L) != 0L)
            return jjStopAtPos(8, 27);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa10_0(active0, 0x1000000000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa11_0(active0, 0x1000000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 103:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStopAtPos(11, 36);
         break;
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 78;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 50)
                        kind = 50;
                     jjCheckNAddStates(0, 2);
                  }
                  else if ((0x842000000000L & l) != 0L)
                  {
                     if (kind > 46)
                        kind = 46;
                  }
                  else if ((0x280000000000L & l) != 0L)
                  {
                     if (kind > 45)
                        kind = 45;
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                  }
                  else if (curChar == 35)
                  {
                     if (kind > 55)
                        kind = 55;
                     jjCheckNAdd(39);
                  }
                  else if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 36;
                  if (curChar == 45)
                     jjCheckNAdd(34);
                  else if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 78:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(71, 77);
                  else if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 76;
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 52)
                        kind = 52;
                     jjCheckNAdd(70);
                  }
                  break;
               case 1:
                  if (curChar == 10 && kind > 16)
                     kind = 16;
                  break;
               case 2:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 31:
                  if ((0x280000000000L & l) != 0L && kind > 45)
                     kind = 45;
                  break;
               case 32:
                  if ((0x842000000000L & l) != 0L && kind > 46)
                     kind = 46;
                  break;
               case 33:
                  if (curChar == 45)
                     jjCheckNAdd(34);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAdd(34);
                  break;
               case 35:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 36;
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 54)
                     kind = 54;
                  jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 38:
                  if (curChar != 35)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjCheckNAdd(39);
                  break;
               case 39:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjCheckNAdd(39);
                  break;
               case 43:
                  if (curChar == 54)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 44:
                  if (curChar == 49)
                     jjstateSet[jjnewStateCnt++] = 43;
                  break;
               case 45:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               case 49:
                  if (curChar == 54)
                     jjstateSet[jjnewStateCnt++] = 48;
                  break;
               case 50:
                  if (curChar == 49)
                     jjstateSet[jjnewStateCnt++] = 49;
                  break;
               case 51:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 50;
                  break;
               case 54:
                  if (curChar == 56 && kind > 18)
                     kind = 18;
                  break;
               case 55:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 54;
                  break;
               case 63:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 62;
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 50)
                     kind = 50;
                  jjCheckNAddStates(0, 2);
                  break;
               case 66:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(66, 67);
                  break;
               case 67:
                  if (curChar == 46)
                     jjCheckNAdd(68);
                  break;
               case 68:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 51)
                     kind = 51;
                  jjCheckNAdd(68);
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(70);
                  break;
               case 71:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(71, 77);
                  break;
               case 77:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 76;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 52)
                        kind = 52;
                     jjCheckNAddStates(3, 5);
                  }
                  if (curChar == 85)
                     jjAddStates(6, 9);
                  else if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 29;
                  else if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 26;
                  else if (curChar == 66)
                     jjstateSet[jjnewStateCnt++] = 23;
                  else if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 78:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(71, 77);
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 52)
                        kind = 52;
                     jjCheckNAdd(70);
                  }
                  break;
               case 3:
                  if (curChar == 78 && kind > 17)
                     kind = 17;
                  break;
               case 4:
               case 16:
                  if (curChar == 65)
                     jjCheckNAdd(3);
                  break;
               case 5:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 6:
                  if (curChar == 68)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 7:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 9:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 12:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 15:
                  if (curChar == 76)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 17:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 68)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 21:
                  if (curChar == 95)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 22:
                  if (curChar == 71)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 23:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 24:
                  if (curChar == 66)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 100 && kind > 19)
                     kind = 19;
                  break;
               case 26:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 25;
                  break;
               case 27:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if (curChar == 68 && kind > 19)
                     kind = 19;
                  break;
               case 29:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 36:
               case 37:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 54)
                     kind = 54;
                  jjCheckNAdd(37);
                  break;
               case 39:
                  if (kind > 55)
                     kind = 55;
                  jjstateSet[jjnewStateCnt++] = 39;
                  break;
               case 40:
                  if (curChar == 85)
                     jjAddStates(6, 9);
                  break;
               case 41:
                  if (curChar == 69 && kind > 18)
                     kind = 18;
                  break;
               case 42:
                  if (curChar == 76)
                     jjCheckNAdd(41);
                  break;
               case 46:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 47:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 46;
                  break;
               case 48:
                  if (curChar == 66)
                     jjCheckNAdd(41);
                  break;
               case 52:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 51;
                  break;
               case 53:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 52;
                  break;
               case 56:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 55;
                  break;
               case 57:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 56;
                  break;
               case 58:
                  if (curChar == 73 && kind > 18)
                     kind = 18;
                  break;
               case 59:
                  if (curChar == 73)
                     jjstateSet[jjnewStateCnt++] = 58;
                  break;
               case 60:
                  if (curChar == 67)
                     jjstateSet[jjnewStateCnt++] = 59;
                  break;
               case 61:
                  if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 60;
                  break;
               case 62:
                  if (curChar == 65)
                     jjstateSet[jjnewStateCnt++] = 61;
                  break;
               case 64:
                  if (curChar == 83)
                     jjstateSet[jjnewStateCnt++] = 63;
                  break;
               case 69:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAddStates(3, 5);
                  break;
               case 70:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(70);
                  break;
               case 71:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(71, 77);
                  break;
               case 72:
                  if (curChar == 101 && kind > 53)
                     kind = 53;
                  break;
               case 73:
                  if (curChar == 117)
                     jjstateSet[jjnewStateCnt++] = 72;
                  break;
               case 74:
                  if (curChar == 108)
                     jjstateSet[jjnewStateCnt++] = 73;
                  break;
               case 75:
                  if (curChar == 97)
                     jjstateSet[jjnewStateCnt++] = 74;
                  break;
               case 76:
                  if (curChar == 118)
                     jjstateSet[jjnewStateCnt++] = 75;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 39:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 55)
                     kind = 55;
                  jjstateSet[jjnewStateCnt++] = 39;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 78 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   34, 66, 67, 70, 71, 77, 47, 53, 57, 64, 
};

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, "\163\164\162\151\156\147", "\151\156\164", 
"\154\157\156\147", "\142\171\164\145", "\163\150\157\162\164", "\144\157\165\142\154\145", 
"\146\154\157\141\164", "\165\142\171\164\145", "\165\163\150\157\162\164", "\165\151\156\164", 
"\165\154\157\156\147", "\142\151\164\163", "\142\171\164\145\163", null, null, null, null, "\50", 
"\51", "\173", "\175", "\54", "\47", "\56\152\165\155\160", 
"\56\163\145\164\157\162\144\145\162", "\56\162\145\160\145\141\164", "\56\147\162\157\165\160", 
"\56\163\153\151\160", "\56\151\146", "\56\163\145\164", "\56\160\162\151\156\164", 
"\56\145\154\163\145", "\56\155\141\162\153", "\56\163\145\164\145\156\143\157\144\151\156\147", 
"\56\144\145\146\151\156\145", "\56\151\156\143\154\165\144\145", "\75\75", "\76", "\74", "\41\75", "\76\75", 
"\74\75", null, null, null, null, null, null, null, null, null, null, null, };

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0xfc7ffffffffff9L, 
};
static final long[] jjtoSkip = {
   0x6L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[78];
private final int[] jjstateSet = new int[156];
protected char curChar;
/** Constructor. */
public SpiffParserTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}

/** Constructor. */
public SpiffParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 78; i-- > 0;)
      jjrounds[i] = 0x80000000;
}

/** Reinitialise parser. */
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}

/** Switch to specified lex state. */
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100000200L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

}
