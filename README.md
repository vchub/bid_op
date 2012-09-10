
# Bid Optimizer



### Github registration
You're there. Register.


### git
Installing on Windows http://code.google.com/p/msysgit
Basics: http://git-scm.com/book/en/Getting-Started-Git-Basics
Getting from Repository: http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository

Basically:
1. install git
1. clone dev branch to your computer
1. add, change files
1. push changes to the repository



### Eclipse 3.7
Eclipse 3.7: http://www.eclipse.org/downloads/packages/release/indigo/sr2

### Scala IDE for Eclipse
http://scala-ide.org/download/current.html
update site link: http://download.scala-ide.org/releases-29/stable/site

Requirements
JDK 5 or JDK 6 (JDK 7 can be used with some caveats).
Eclipse, including the JDT. â€œEclipse Classicâ€� is sufficient, but any Eclipse package can be used.
Both Eclipse 3.6 (Helios) and Eclipse 3.7 (Indigo) are supported.

I tried it wiht Eclipse 4 (Juno). It wasn't a very good experience.

### Project structure
Analytical module source dir - app/models/optimizer/

### Tests
dir:  test/models/optimizer/

There are a couple JUnit test examples. I gonna add more soon.


download git for windows
http://cloud.github.com/downloads/msysgit/git/Git-1.7.11-preview20120710.exe
it has graphical interface
put in редактировать-настройки user name: ... email: ....


download play framework
http://download.playframework.org/releases/play-2.0.3.zip
uzip
add play dir to PATH variable. Like PATH;c:\vlad\bin\play-2.0.3

clone project from repo:

in c:\vlad\code> git clone https://github.com/vchub/bid_op.git

>cd bid_op 	// cd to the new dir

>play // run the play framework:

[]# compile 	// compile project

[]# eclipsify	// run command eclipsify. it prepares eclipse project

open eclipse and import the project.

it looks ugly though. many jars stay on root level. I don't know how change it right now






