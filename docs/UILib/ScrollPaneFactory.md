# ScrollPaneFactory

## What is it?
A factory (creator) class for `JScrollPane`. 

The `createPane()` method returns the `JScrollPane` with scrollbars visible.

The `createHideScroll()` method returns the `JScrollPane` with scrollbars invisible.

## Sample usage: 
```java
// Make you have appropriate imports 
JScrollPane scrollPane = ScrollPaneFactory.createPane(); // Creates the JScrollPane

// You can proceed to do normal actions to the JScrollPane
scrollPane.setViewportView(body);
scrollPane.getViewport().setBackground(BACKGROUND);
```

--- 
### Extra Reading 
- [Oracle](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JScrollPane.html)
- [Oracle Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html)
- [GeekforGeeks](https://www.geeksforgeeks.org/java/java-jscrollpane/)
- [Factory Design Pattern](https://refactoring.guru/design-patterns/factory-method)
