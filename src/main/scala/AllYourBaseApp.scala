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
    object outputTextArea extends TextArea {
      editable = false
    }
    object statusTextField extends TextField {
      maximumSize = new Dimension(Short.MaxValue, preferredSize.height)
      editable = false
    }

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
          // For reasons I do not understand, if we throw an Exception of a custom subclass of Exception,
          // the exception here will be a PrivilegedActionException wrapping our exception, and you can
          // retrieve the exception we actually want using getCause
          case e: Throwable => setStatusText(e.getMessage)
        }
      }
    }
    Toolkit.getDefaultToolkit.getSystemEventQueue.push(new ErrorHandlingEventQueue())

    def setStatusText(status : String) {
      statusTextField.text = "Error: %s".format(status)
    }

    def processInput () {
      val lines = inputTextArea.text.lines

      if (!lines.hasNext) {
        sys.error("Input cannot be empty")
      }

      var count = 0
      try {
        count = lines.next().toInt
      } catch {
        case _ : NumberFormatException =>
          sys.error("First line of input must be a number equal to remaining input lines")
      }

      val data = lines.take(count).toArray
      if (count != data.length) {
        sys.error("length of data and count must be equal")
      }
      val results = AllYourBaseWorker.process(data)
      val outputs = results.zipWithIndex.map{ case (result, i) => "Case #%d: %d".format(i+1, result)}
      outputTextArea.text = outputs.mkString("\n")
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
            parallel(Alignment.Center)(
              processButton,
              // I think supposed to keep text box at vertical defaults at all times, but doesn't work
              ComponentItem(statusTextField, DefaultSizes))
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
