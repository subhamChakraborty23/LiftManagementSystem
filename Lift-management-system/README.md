## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## References :
- https://medium.com/geekculture/system-design-elevator-system-design-interview-question-6e8d03ce1b44 : from this article i got to know about the approach of designing the lift as threads which will will work as worker and the manager thread will manage the threads.
- https://www3.ntu.edu.sg/home/ehchua/programming/java/j5e_multithreading.html : used this article to implement some of the parts of the code that needed multiple threads 
- https://coderanch.com/t/707819/engineering/Design-Elevator-System : used this article to get the idea of how the classes should be designed
- https://www.careercup.com/question?id=5698327039442944 : used this aricle to get the idea of different ways to to store the state of different requests such as up and down requests ,here it is using priority queue but i have trie to use a map to store the requests and set for floor details but used some of the methods mentioned here and changed some of the internal implementation .
- https://medium.com/geekculture/system-design-elevator-system-design-interview-question-6e8d03ce1b44 : used this article and got an idea from the pseudo code and tried to implement some of it this problem and also got the idea to use singleton design pattern for the manager thread.
