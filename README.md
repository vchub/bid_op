
# Bid Optimizer



### Github registration
You're there. Register.


### git
* Installing on Windows http://git-scm.com/downloads

* Windows git has graphical interface
* Add global settings - put in редактировать-настройки user name: ... email: ....

Help:
* Basics: http://git-scm.com/book/en/Getting-Started-Git-Basics
* Getting from Repository: http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository


Basically:
1. install git
1. clone dev branch to your computer
1. add, change files
1. pull current state of the branch fro repository (git pull origin dev)
1. push changes to the repository
1. continue adding and changing files



### Eclipse 3.7
Eclipse 3.7: http://www.eclipse.org/downloads/packages/release/indigo/sr2

### Scala IDE for Eclipse
* http://scala-ide.org/download/current.html
* update site link: http://download.scala-ide.org/releases-29/stable/site

Requirements
* JDK 5 or JDK 6 (JDK 7 can be used with some caveats)
* Eclipse, including the JDT. Eclipse Classic is sufficient
* Both Eclipse 3.6 (Helios) and Eclipse 3.7 (Indigo) are supported

I tried it wiht Eclipse 4 (Juno). It wasn't a very good experience. ON the other hand it may be fixed already.

### Play framework
* Play framework is the application framework used in the project
* download play framework from http://download.playframework.org/releases/play-2.0.3.zip
* uzip to the work dir
* add play dir to PATH variable. Like PATH;c:\vlad\bin\play-2.0.3

### Prepare the Eclipse project
* clone project from repo like:
      c:\vlad\code> git clone https://github.com/vchub/bid_op.git
* cd to the new dir
      >cd bid_op
* run play framework
      >play
* compile project
      [bid]# compile
* run command eclipsify. it prepares eclipse project
      []# eclipsify
* open eclipse and import the project.

It looks ugly though. many jars stay on root level. I don't know how change it right now

### Project structure
* Most of the source code is in bid_op/app
* Analytical module in bid_op/app/models/optimizer
* The project is built by sbt (simple built tool). To run the project and complete stack of Tests "play" command can be used, like
      >play
      [bid]# ~compile   //to compile on every change in source files
      [bid]# test       // to run tests


### Tests
* Tests are in bid_op/test/models/optimizer
* From Eclipse only JUnit tests can run now. For scala tests sbt has to be used

There are a couple JUnit test examples. I gonna add more soon.







