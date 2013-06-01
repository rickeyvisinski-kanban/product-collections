package org.catch22
import collections.immutable.{CollSeq1,CollSeq2}
import collections.util.{Stats,WeightedStats}

/**
 * ==A strongly typed two dimentional data framework.==
 * 
 *  [[collections.CollSeq]] is an IndexedSeq[Product] that also implements
 *  Product itself.  
 *  
 *  Specialized versions of CollSeq exist for arities 1 to 22.  Each is an 
 *  IndexedSeq[ProductN] and also implements ProductN
 *  
 *  ===I/O===
 *  
 *  [[io.CsvParser]] is a very easy way to read CollSeqs from the File System.
 *  
 *  You use the factory to select a parser:
 *  
 *  {{{val parser= CsvParser[String, Int, Double]}}}
 *  
 *  and read your file like this:
 *  
 *  {{{val data= parser.parseFile("example.csv")}}}
 *  
 *  You wind up with CollSeq3[String,Int,Double]
 *  
 *  ===Positioning===
 *  
 *  product-collections aims to be simple and productive:  you should be 
 *  producing answers from your data in 20 minutes or less.  There is no 
 *  new api to learn -- everything works like a scala collection and a Tuple
 *  at the same time.  There's no matrix arithmetic: do everything in idomatic 
 *  scala.  
 *  
 *  Columns don't lose their type if you include a column of another type.  
 *  '
 *  Learn by example: take a look (or clone) the 
 *  simple example project on Gitub that does some simple processing of 
 *  stock prices.
 *  
 *  ===Alternatives===
 *  
 *  ====Saddle====
 *  A heavy duty solution.  Custom api based around Vectors 
 *  Matrixes and Scalars.  Trying to mix types in a Saddle matrix results in
 *  a Matrix[Any] which means not much type safety.  Saddle seems to
 *  have garnered some support from Typesafe and may feature in GSOC.
 *  
 *  Saddle has heavy emphasis on specialization and (presumably) performance.
 *  
 *  ====Breeze====
 *  
 *  Breeze also has matrix and vector implementations similar to Saddle.  Also 
 *  some other stuff that looks pretty useful.
 *  
 */
package object collections {
  
  /**Convert a Seq[Product1] to a CollSeq*/
  implicit def SeqToCollSeqTa[T](s:Seq[Product1[T]])=CollSeq1[T](s)
  
  /**Convert a Seq[Product2] to a CollSeq*/
  implicit def SeqToCollSeqTb[Ta,Tb](s:Seq[Product2[Ta,Tb]])=CollSeq2[Ta,Tb](s)
  
  /**Convert a Seq[Any] to a CollSeq*/
  implicit def SeqToCollSeqTc[T](s:Seq[T])=CollSeq1(s.map(Tuple1[T](_)))
  
  type CollSeq=immutable.CollSeq
  val CollSeq=immutable.CollSeq
  
  /**Convert a Seq[Numeric] to a Stats object*/
  implicit def SeqAToStatsA[A](s:Iterable[A])(implicit num:Numeric[A])= new Stats[A](s)
  
  /**Convert a Seq[(Numeric,Numeric)] to a WeightedStats object*/
  implicit def SeqABToWeightedStatsAB[A,B](s:Iterable[Product2[A,B]])(implicit numA:Numeric[A], numB:Numeric[B])= new WeightedStats[A,B](s)
}