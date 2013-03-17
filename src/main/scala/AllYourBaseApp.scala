package main.scala

import swing._
import event._

/**
 * @author Fairfax
 * @since 2013-03-16 4:21 PM
 */
object AllYourBaseApp extends App {

  override def main (args:Array[String]) {
    Gui.main(args)
  }

  object Gui extends SimpleSwingApplication {
    object processButton extends Button("Process")
    object inputTextArea extends TextArea
    object outputTextArea extends TextArea
    object statusTextField extends TextField

    listenTo(inputTextArea)

    reactions += {
      case ButtonClicked(`processButton`) =>
        processInput()
    }

    def processInput () {
      val inputs = inputTextArea.text.lines
      val count = inputs.next().toInt
      for (input <- 0 until count) {
        if (inputs.hasNext) {
          val input = inputs.next()
          outputTextArea.text = input
        }
      }
    }

    def top = new MainFrame {
      title = "All Your Base - Google Code Jam"
      preferredSize = new Dimension(500, 600)
      contents = new BoxPanel(Orientation.Vertical) {
        contents += new SplitPane(Orientation.Vertical,
          new BoxPanel(Orientation.Vertical) {
            contents += new Label("In")
            contents += new ScrollPane(inputTextArea)
          },
          new BoxPanel(Orientation.Vertical) {
            contents += new Label("Out")
            contents += new ScrollPane(outputTextArea)
          }) {
          resizeWeight = 0.5
        }
        contents += processButton
      }
      centerOnScreen()
    }
  }
}
