# TextPaneBuilder

## What is it?
A concrete builder class for `JTextPane`. 

Each setter returns `this`,a reference back to the builder, allowing for
chaining. 

The `build()` method returns the `JTextPane`.

## Sample usage: 
```java
// Make you have appropriate imports 

JTextPane textPane = new TextPaneBuilder()
    .setText("Flight Analyser") // Set the text in button to "Flight Analyser"
    .setforeground(TEXT_COLOR)  // Set foreground to TEXT_COLOR
    .setFont(BOLD_FONT)         // Set font to BOLD_FONT
    .setFontSize(48)            // Set fontsize to 48
    .build();

textPane.setBorder(null);       // You can still proceed to do normal to the
                                // JTextPane
```

> [!NOTE]
> There is no need to use all the setters, there is nice default value with the
> builder.

## Tricks 
After you created your `textPane`, you center the text in `textPane` with this:
```java 
StyledDocument doc = textPane.getStyledDocument();
SimpleAttributeSet center = new SimpleAttributeSet();
StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
doc.setParagraphAttributes(0, doc.getLength(), center, false);
```

--- 
### Extra Reading

- [Oracle](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JTextPane.html)
- [Oracle Tutorials](https://docs.oracle.com/javase/tutorial/uiswing/components/editorpane.html)
- [GeekforGeeks](https://www.geeksforgeeks.org/java/java-jtextpane/)
- [Builder Design Pattern](https://refactoring.guru/design-patterns/builder)
