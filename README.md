# TuiTest2 - Use Case for the new Executor Service instead of vanilla threads.
Adds large number of files that have numbers in them all in parallel and them computes their total. JDK 5 Concurrency Executor Used.Immutability Pattern used. Atomic Integer used for Shared access. Factory pattern used for doling out instances safely via synchronization.
<br />
<br />To run this demo use the <b>main</b> method of <b>FileSumUp.java</b>
<br />
<br />The input files are all provided in the <b>"/tui_data/"</b> folder which is part of this repository
