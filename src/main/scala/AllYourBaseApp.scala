package main.scala

import swing._
import event._
import java.awt.{Toolkit, AWTEvent, EventQueue, Adjustable}
import javax.swing._

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
        val inLabel = new Label("In")
        val inScroll = new ScrollPane(inputTextArea)
        val outLabel = new Label("Out")
        val outScroll = new ScrollPane(outputTextArea)
        val splitPane = new SplitPane(Orientation.Vertical,
          new BoxPanel(Orientation.Vertical) {
            contents += inLabel
            contents += inScroll
          },
          new BoxPanel(Orientation.Vertical) {
            contents += outLabel
            contents += outScroll
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
//    val top = new Frame {
//      contents = new GroupPanel {
//        val label1 = new Label("Label 1")
//        val label2 = new Label("Label 2")
//        val text1 = new TextField
//        val text2 = new TextField
//        autoCreateGaps = true
//        autoCreateContainerGaps = true
//        horizontalGroup {
//          sequential(
//            parallel()(label1, label2),
//            parallel()(text1, text2)
//          )
//        }
//        verticalGroup {
//          sequential(
//            parallel(Alignment.Baseline)(label1, text1),
//            parallel(Alignment.Baseline)(label2, text2)
//          )
//        }
//      }
  }
}
