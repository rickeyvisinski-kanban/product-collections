#product-collections
===============

A simple, strongly typed framework for working with 2D data in scala.  CollSeq is in essence a spreadsheet like 
data structure that you can manipulate as a scala collection.

##Using CollSeq

### Scaladoc

View the [Scaladoc](http://marklister.github.io/product-collections/target/scala-2.10.1/api)

###Creating a CollSeq

To create a CollSeq normally you let the compiler infer the appropriate implementation:

    scala> CollSeq(("A",2,3),("B",3,4),("C",4,5))
    res1: org.catch22.collections.immutable.CollSeq3[java.lang.String,Int,Int] = 
    CollSeq((A,2,3),
            (B,3,4),
            (C,4,5))


###Extracting columns:

A CollSeq is also a Product (essentially a Tuple). To extract a column:

    scala> res1._2
    res2: Seq[Int] = List(2, 3, 4)

###Extract a row

CollSeq is an IndexedSeq so you can extract a row in the normal manner:

    scala> res1(1)
    res3: Product3[java.lang.String,Int,Int] = (B,3,4)

### Add a column

You can use the flatZip method to add a column:

    scala> res1.flatZip(res1._2.map(_ *2))
    res14: org.catch22.collections.immutable.CollSeq4[java.lang.String,Int,Int,Int] = 
    (A,2,3,4)
    (B,3,4,6)
    (C,4,5,8)

### Access the row "above"

Using scala's sliding method you can access the preceeding n rows.  Here we calculate the difference between the values in the 4th column:

    scala> res14._4.sliding(2).toList.map(z=>z(1)-z(0))
    res21: List[Int] = List(2, 2)```

Append the result:

    scala> res14.flatZip(0::res21)
    res22: org.catch22.collections.immutable.CollSeq5[java.lang.String,Int,Int,Int,Int] = 
    (A,2,3,4,0)
    (B,3,4,6,2)
    (C,4,5,8,2)

### Splice columns together

This uses the implicit conversions in the collections package object.

    scala> CollSeq((1,2,3),(2,3,4),(3,4,5))```
    res0: org.catch22.collections.immutable.CollSeq3[Int,Int,Int] = 
    CollSeq((1,2,3),
            (2,3,4),
            (3,4,5))

    scala> res0._3 flatZip res0._1 flatZip res0._2
    res2: org.catch22.collections.immutable.CollSeq3[Int,Int,Int] = 
    CollSeq((3,1,2),
            (4,2,3),
            (5,3,4))
            
##IO

The CsvParser class (and its concrete sub-classes) allow you to easily read CollSeqs from the filesystem.

###Construct a Parser

    scala> val parser=CsvParser[String,Int,Int,Int]
    parser: org.catch22.collections.io.CsvParser4[String,Int,Int,Int] = org.catch22.collections.io.CsvParser4@1203c6e

###Read and Parse a file
    scala> parser.parseFile("abil.csv",hasHeader=true,delimiter="\t")
    res2: org.catch22.collections.immutable.CollSeq4[String,Int,Int,Int] = 
    CollSeq((30-APR-12,3885,3922,3859),
            (02-MAY-12,3880,3915,3857),
            (03-MAY-12,3920,3948,3874),
            (04-MAY-12,3909,3952,3885),
            (07-MAY-12,3853,3900,3825),
            (08-MAY-12,3770,3851,3755),
            (09-MAY-12,3700,3782,3666),
            (10-MAY-12,3732,3745,3658),
            (11-MAY-12,3760,3765,3703),
            (14-MAY-12,3660,3750,3655),
            (15-MAY-12,3650,3685,3627),
            (16-MAY-12,3661,3663,3555),
            (17-MAY-12,3620,3690,3600),
            (18-MAY-12,3545,3595,3542),
            (21-MAY-12,3602,3608,3546),
            (22-MAY-12,3650,3675,3615),
            (23-MAY-12,3566,3655,3566),
            (24-MAY-12,3632,3645,3586),
            (25-MAY-12,3610,3665,3583),
            (28-MAY-12,3591,3647,3582),
         ...
###Parsing additional types
To parse additional types (like dates) simply provide a converter as an implicit parameter.  See the examples.

##Examples

###Read Stock prices and calculate moving average
An example REPL session.  Let's read some stock prices and calculate the 5 period moving average:

    scala> import java.util.Date
    import java.util.Date

    scala> implicit val dmy = new DateConverter("dd-MMM-yy")  // tell the parser how to read your dates
    dmy: org.catch22.collections.io.DateConverter = org.catch22.collections.io.DateConverter@26d606

    scala> val p=CsvParser[Date,Int,Int,Int,Int]  //Date, close, High, Low, Volume
    p: org.catch22.collections.io.CsvParser5[java.util.Date,Int,Int,Int,Int] = org.catch22.collections.io.CsvParser5@1584d9

    scala> val prices=p.parseFile("abil.csv", hasHeader=true, delimiter="\t")
    prices: org.catch22.collections.immutable.CollSeq5[java.util.Date,Int,Int,Int,Int] = 
    (Mon Apr 30 00:00:00 AST 2012,3885,3922,3859,4296459)
    (Wed May 02 00:00:00 AST 2012,3880,3915,3857,3127464)
    (Thu May 03 00:00:00 AST 2012,3920,3948,3874,3080823)
    (Fri May 04 00:00:00 AST 2012,3909,3952,3885,2313354)
    (Mon ....

    scala> val ma= prices._2.sliding(5).toList.map(_.mean)
    ma: List[Double] = List(3889.4, 3866.4, 3830.4, 3792.8, 3763.0, 3724.4, 3700.4, 3692.6, 3670.2, 3627.2, 3615.6, 3615.6, 3596.6, 3599.0, 3612.0, 3609.8, 3605.6, 3611.0, 3611.0, 3606.0, 3614.2, 3612.4, 3629.0, 3634.6, 3659.4, 3661.0, 3657.2, 3645.2, 3628.4, 3616.4, 3632.8, 3668.8, 3702.6, 3745.4, 3781.0, 3779.6, 3755.4, 3727.4, 3689.4, 3650.2, 3638.8, 3641.8, 3648.2, 3663.2, 3671.0, 3649.4, 3624.4, 3595.0, 3559.0, 3518.0, 3505.8, 3495.8, 3505.8, 3531.2, 3570.8, 3589.0, 3613.0, 3620.8, 3624.4, 3635.4, 3661.0, 3667.0, 3686.6, 3703.6, 3720.0, 3722.4, 3692.4, 3619.0, 3553.4, 3473.4, 3413.2, 3400.0, 3422.8, 3427.4, 3433.6, 3434.0, 3425.6, 3403.8, 3396.6, 3388.6, 3376.0, 3353.6, 3318.6, 3291.8, 3260.6, 3240.0, 3225.0, 3226.0, 3218.2, 3232.2, 3219.6, 3226.0, 3234.0, 3251.0, 3271.0, 3312.4, 3341....

    scala> prices._1.drop(5).zip(ma) //moving average zipped with date
    res0: Seq[(java.util.Date, Double)] = List((Tue May 08 00:00:00 AST 2012,3889.4), (Wed May 09 00:00:00 AST 2012,3866.4), (Thu May 10 00:00:00 AST 2012,3830.4), (Fri May 11 00:00:00 AST 2012,3792.8), (Mon May 14 00:00:00 AST 2012,3763.0), (Tue May 15 00:00:00 AST 2012,3724.4), (Wed May 16 00:00:00 AST 2012,3700.4), (Thu May 17 00:00:00 AST 2012,3692.6), (Fri May 18 00:00:00 AST 2012,3670.2), (Mon May 21 00:00:00 AST 2012,3627.2), (Tue May 22 00:00:00 AST 2012,3615.6), (Wed May 23 00:00:00 AST 2012,3615.6), (Thu May 24 00:00:00 AST 2012,3596.6), (Fri May 25 00:00:00 AST 2012,3599.0), (Mon May 28 00:00:00 AST 2012,3612.0), (Tue May 29 00:00:00 AST 2012,3609.8), (Wed May 30 00:00:00 AST 2012,3605.6), (Thu May 31 00:00:00 AST 2012,3611.0), (Fri Jun 01 00:00:00 AST 2012,3611.0), (Mon Jun 04 0...
    scala> 

####(Contrived) Example: calculate an aircraft's moment in in-lb 

    scala> val aircraftLoading=CollSeq(("Row1",86,214),("Row4",168,314),("FwdCargo",204,378)) //Flight Station, Mass kg, Arm in
    aircraftLoading: org.catch22.collections.immutable.CollSeq3[java.lang.String,Int,Int] = 
    (Row1,86,214)
    (Row4,168,314)
    (FwdCargo,204,378)

    scala> val pounds = aircraftLoading._2.map(_ * 2.2)  //convert kg -> lb
    pounds: Seq[Double] = List(189.20000000000002, 369.6, 448.8)

    scala> val moment = pounds.zip(aircraftLoading._3).map(x=>x._1*x._2)
    moment: Seq[Double] = List(40488.8, 116054.40000000001, 169646.4)

    scala> moment.sum
    res1: Double = 326189.6


##Architecture
===========

CollSeq is a wrapper around an ordinary scala IndexedSeq[Product].    CollSeq also implements Product itself.

CollSeqN are concrete implementations of CollSeq.  They extend IndexedSeq[ProductN[T1,..,TN]] and implement ProductN.

CsvParser is a simple Csv reader/parser that returns a CollSeqN

CollSeqN has only one novel method: ```flatZip (s:Seq[A]): CollSeqN+1[T1,..TN,A]```

There are implicit conversions from Seq[Product1[T]] to CollSeq[T] and from Seq[Product2[T1,T2]] to CollSeq[T1,T2]
There is also an implicit conversion from Seq[T] to CollSeq[T]


## Status

Beta.  But I'm using it internally. At present the api contains only a single novel method.  It's probably safe to regard the existing api as stable.  

##Future

In no particular order:

*  Publish to a repo somewhere.
*  A similar wrapper around Map.
*  A Proper Stats implementation preferably as a library dependancy.

##Include in your project

You can use un unmanaged jar: [Scala-2.10](http://marklister.github.io/product-collections/target/scala-2.10/product-collections_2.10-0.0.1-SNAPSHOT.jar) 

Alternatively:
* Build from source
* sbt> publish-local
* add ```libraryDependencies += "org.catch22" %% "product-collections" % "0.0.1-SNAPSHOT"``` to your ```build.sbt```
* import org.catch22.collections._ and/or org.catch22.collections.io

##Build

* git clone git://github.com/marklister/product-collections.git
* sbt
* sbt> compile
* sbt> test
* sbt> console

sbt will fetch a modified source of ```sbt-boilerplate```  
Once sbt-boilerplate incorporates the requisite pull request I'll switch to a binary dependancy.

##Sample Projects

I'll post a sample that creates a graph of share prices with a moving average in about a dozen lines of code.

##Licence

Apache2 however currently the Csv.scala contains about 30 lines of code licenced under the GPL 3.  If necessary rewriting Csv.scala (or obtaining another licence) should be pretty easy.
