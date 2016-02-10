# WTF is git

Git is a version controll system. A version controll system is a system
that helps versioning changes.

In our case git is the (core) program that does it. So in ELI5 terms: Git
decides which changes came before and which changes came later.

It also handles merges of changes. So if person A modified file F and person
B also modified the same file than git can figure out most of the time which
changes should be there (for example if they modified different parts of the
file).

Git goes even further by doing this for entire directories, and subdirectories etc.

## Keep in mind

Git is stupid. In fact its so stupid that its called a `git`. You'll hate git
when you start working wit hit at first, thats OK, git doesn't mind.

## How does git help us?

It makes merging of changes faster. Remember how you used to work together on
reports and send versions to eachother, ie report-1 report-2 report-2a etc and
when you had to hand it in it would become report-5-finalversion, but then
your friend made a change and he called it report-5-finalversion-2, well git
fixes this madness.

Now for your average programming project you'll quickly have 10 to 20 files
lying around (especially for Java cause it forces you to jam classes in new files).

# How to make a new version in Git
First add the files you want to put into the new version with `git add`

Use `git commit`, by making a new version you create an anchor, where you say
these chagnes are important. Git will ask you to give a little message descrbing
the chagne, just keep it brief and to the point.

# Git architecture

Git is a distributed system, meaning that everyon will have there own little
version controll system and an entire copy off all the history.

We use github as a server, however we could've used any arbitrary computer.
In fact you can push and pull changes from your local file system.

##
