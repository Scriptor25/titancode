# TitanCode

## About

TitanCode (the wip title) is a simple programming language run by an interpreter based on Java. It has a highly functional character, e.g. in contrast to the fully object oriented Java language.

## Features

 - multiline comment delimiter: ```#```
 - no types
 - no semicolons
 - define functions: ```def <name>(<arg>...) = ...```
 - define variables: ```def <name> = ...```
 - functions can have variadic arguments
 - for-each-in-range expressions: ```[<from>, <to>]{<var>} ...```, ```{<var>}``` is optional
 - return value of a function is always the value of its last expression
 - function calls: ```<name>(<arg>...)```
 - use of native Java functions: ```native("<name>", <arg>...)```
 - use variadic args:
    - get all: ```?```
    - get by index: ```?{<index>}```

## WIP

 - logic flow: if, while, switch
 - file includes
 - early return, break, continue
 - char "type"
 - real arrays and objects
