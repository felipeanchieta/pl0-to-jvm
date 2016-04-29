# pl0-to-jvm

a translator of pl/0 (subset of ibm pl/I) to java bytecode

# how to contribute

first rule: utilise git. there are implementations for linux, windows and mac osx, so just download it https://git-scm.com/downloads

I assure that you have internet access to learn how to configure your account into git (gui or bash)

second rule: it's described below the process for git under bash. google for "git gui" if you use gui.

a) git clone https://github.com/felipeanchieta/pl0-to-jvm.git

b) cd pl0-to-jvm

after writing some code, test and commit

c) git commit -m "<message>"

it's obvious that <message> should be replaced by a message that describe what you've done

after one or more commits, you PUSH the commits to the GitHub server:

d) git push origin master

moreover, the code is under heavy development, so it's necessary that you PULL the updates frequently

e) git pull origin master

you can open issues in GitHub as well, so I can answer all of your questions.

# how to build using Ant

if you prefer use Ant, I've already written a build.xml for that. just check if you have Apache Ant installed in your computer:

ant --version

after that, you can use the following command to compile the code:

ant all

if you're sure that build.xml is wrong, DON'T use it. it's clear that if you do

javac src/*.java

you will compile everything.

WARNING: 

1) do not COMMIT any compiled .class

2) use git add "path and name of the file" to add new files to your next COMMIT

