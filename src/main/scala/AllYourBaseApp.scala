package main.scala

import swing._
import event._
import java.awt.{Toolkit, AWTEvent, EventQueue}

/**
 * @author Carl Gieringer
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

    listenTo(processButton)

    reactions += {
      case ButtonClicked(`processButton`) =>
        statusTextField.text = "Processing..."
        processInput()
        statusTextField.text = "Done."
    }

    class ErrorHandlingEventQueue extends EventQueue {
      override def dispatchEvent(newEvent: AWTEvent) {
        try {
          super.dispatchEvent(newEvent)
        } catch {
          case e: Exception => statusTextField.text =
            "Error: %s".format(e.getLocalizedMessage)
        }
      }
    }
    Toolkit.getDefaultToolkit.getSystemEventQueue
      .push(new ErrorHandlingEventQueue())

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

    val top = new MainFrame {
      title = "All Your Base - Google Code Jam"
      preferredSize = new Dimension(500, 600)
      contents = new GroupPanel {
        val splitPane = new SplitPane(Orientation.Vertical,
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
        verticalGroup {
          sequential(
            splitPane,
            parallel(Alignment.Center)(processButton, statusTextField)
          )
        }
        horizontalGroup {
          parallel()(
            splitPane,
            sequential(processButton, statusTextField)
          )
        }
      }
      centerOnScreen()
    }
  }
}
