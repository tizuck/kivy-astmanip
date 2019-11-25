package model

import com.github.python3parser.model.mods.ExpressionMod

object lines {
  trait WidgetBodyElement extends ASTNode
  trait InstructionBodyElement extends ASTNode
  trait CanvasBodyElement extends ASTNode
  trait RootNodeElement extends ASTNode

  object CanvasType {
    trait canvasType
    case object Regular extends canvasType {
      override def toString: String = "canvas"
    }
    case object Before extends canvasType {
      override def toString: String = "canvas.before"
    }
    case object After extends canvasType {
      override def toString: String = "canvas.after"
    }
  }

  type RootLevelNodes = List[RootNodeElement]
  type WidgetBody = List[WidgetBodyElement]
  type InstructionBody = List[InstructionBodyElement]
  type CanvasBody = List[CanvasBodyElement]

  case class TopLevel(rootLevelNodes: RootLevelNodes) extends ASTNode
  case class Directive(directive:String) extends RootNodeElement {
    override def toString: String = s"#:$directive"
  }
  case class Root(widget:Widget) extends RootNodeElement
  case class Widget(name:WName, widgetBody:WidgetBody) extends WidgetBodyElement
  case class Canvas(canvasType: CanvasType.canvasType,canvasBody:CanvasBody) extends WidgetBodyElement
  case class Instruction(name:WName, instructionBody:InstructionBody) extends CanvasBodyElement
  case class Property(name:String, propertyBody:List[Python]) extends WidgetBodyElement with InstructionBodyElement
  case class Comment(comment:String) extends WidgetBodyElement with InstructionBodyElement with CanvasBodyElement with
  RootNodeElement {
    override def toString: String = s"#$comment"
  }
  case class ClassRule(classWidget:AutoClass, widgetBody:List[ASTNode]) extends RootNodeElement
  case class ClassList(names:List[KivyString]) extends ASTNode
  case class ClassListBase(name:List[KivyString]) extends ASTNode
  case class AutoClass(widgetList:ClassList, widgetBase:Option[ClassListBase]) extends ASTNode
  case class Template(classWidget:AutoClass, widgetBody:List[ASTNode]) extends RootNodeElement

  sealed trait KivyString extends ASTNode
  case class WName(name:String) extends KivyString {
    override def toString: String = name
  }
  case class Reset(name:String) extends KivyString {
    override def toString: String = s"-$name"
  }

  case class Python(pCode:String) extends ASTNode {

    lazy val pythonAST: ExpressionMod =
      com
      .github
      .python3parser
      .utilities
      .ASTParser
      .parseExpressionMod(pCode)

    /**
     * @return textual representation of python Code contained in `pCode` ,
     *         but wihtout leading and ending WhiteSpace characters.
     */
    override def toString: String = {
      val firstIndexWithoutWhiteSpaces =
        pCode
        .indexWhere(c => """[^\s]""".r.pattern.matcher(c.toString).matches())

      val lastIndexBeforeWhieSpacesBegin =
        pCode
          .lastIndexWhere(c => """[^\s]""".r.pattern.matcher(c.toString).matches()) + 1

      pCode.substring(
        if(firstIndexWithoutWhiteSpaces != -1) firstIndexWithoutWhiteSpaces else 0,
        if(lastIndexBeforeWhieSpacesBegin != -1) lastIndexBeforeWhieSpacesBegin else pCode.lastIndexOf(pCode.last + 1)
      )
    }
  }
}
