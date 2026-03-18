# TableBuilder

> [!WARNING] 
> Still working in progress

## What is it?
A concrete builder class for `JTable`. 

Each setter returns `this`,a reference back to the builder, allowing for
chaining. 

The `build()` method returns the `JTable`.

The `buildPane()` method returns the `JScrollPane`.
- this is the recommended method

## Sample usage: 
```java
// Make you have appropriate imports 

String[][] data;
String[] columnNames;
// TableBuilder takes in Object[][] and Object[] as parameters
JScrollPane table = new TableBuilder(data, columnNames)
        .setFontSize(24)     // Set fontsize to 24 
        .setFont(BOLD_FONT)  // Set font to BOLD_FONT
        .buildPane();   
table.setPreferredSize(new Dimension(Integer.MAX_VALUE, 800));
table.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
```

> [!NOTE]
> There is no need to use all the setters, there is nice default value with the
> builder.

--- 
### Extra Reading

- [Oracle](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JTable.html)
- [Oracle Tutorials](https://docs.oracle.com/javase/tutorial/uiswing/components/table.html)
- [GeekforGeeks](https://www.geeksforgeeks.org/java/java-swing-jtable/)
- [Builder Design Pattern](https://refactoring.guru/design-patterns/builder)
