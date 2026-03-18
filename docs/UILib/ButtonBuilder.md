# ButtonBuilder 

## What is it?
A concrete builder class for `JButton`. 

Each setter returns `this`,a reference back to the builder, allowing for
chaining. 

The `build()` method returns the `JButton`.

## Sample usage: 
```java
// Make you have appropriate imports 

String imagePath = "/Images/image.png";
JButton button = new ButtonBuilder()
    .setText("Click Me")        // Set the text in button to "Click Me"
    .setBackground(HIGHLIGHT)   // Set background to HIGHLIGHT
    .setforeground(TEXT_COLOR)  // Set foreground to TEXT_COLOR
    .setIcon(imagePath)         // Load the image from the imagePath and set it
                                // ImageIcon for the JButton
    .setFont(BOLD_FONT)         // Set font to BOLD_FONT
    .setBold(false)             // Set bold style to true
    .setItalic(true)            // Set italic style to true
    .setFontSize(48)            // Set fontsize to 48
    .build();

button.setBorder(null);     // You can still proceed to do normal to the
                            // JButton
```

> [!NOTE]
> There is no need to use all the setters, there is nice default value with the
> builder.

--- 
### Extra Reading 
- [Oracle](https://docs.oracle.com/en/java/javase/21/docs/api/java.desktop/javax/swing/JButton.html)
- [Oracle Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/components/button.html)
- [Tutorials Point](https://www.tutorialspoint.com/swing/swing_jbutton.htm)
- [Builder Design Pattern](https://refactoring.guru/design-patterns/builder)
