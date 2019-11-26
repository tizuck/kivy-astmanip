package model

import org.bitbucket.inkytonik.kiama.rewriting.Strategy

/**
 * Highly inspired by [[https://typelevel.org/cats/typeclasses/foldable.html]]
 * However, ASTNode cannot be instantiated as `cats.Foldable` because it does not
 * fit higher kinds.
 */
trait FoldableAST {
  def foldLeft[B](z:B)(op:(B,ASTNode) => B):B
 // def foldRight[B](z:B)(op:(ASTNode,B) => B):B

  def exists(astNode: ASTNode):Boolean =
    foldLeft(false){case (b,ast) => b || ast.equals(astNode)}

  def forall(f: ASTNode => Boolean):Boolean =
    foldLeft(true){case (b,ast) => b && f(ast)}

  def reduceLeftToOption[B](f:ASTNode => B)(g:(B,ASTNode) => B):Option[B] =
    foldLeft(Option.empty[B]){
      case (Some(b),astNode) => Some(g(b,astNode))
      case (None,astNode) => Some(f(astNode))
    }

  /*def reduceRightToOption[B](f:ASTNode => B)(g:(ASTNode,B) => B):Option[B] =
    foldRight(Option.empty[B]){
      case(astNode,Some(b)) => Some(g(astNode,b))
      case (astNode,None) => Some(f(astNode))
    }*/

  def reduceLeftOption(f:(ASTNode,ASTNode) => ASTNode):Option[ASTNode] =
    reduceLeftToOption(identity)(f)

  /*def reduceRightOption(f:(ASTNode,ASTNode) => ASTNode):Option[ASTNode] =
    reduceRightToOption(identity)(f)*/

  def get(index:Long) =
    foldLeft((Option.empty[ASTNode],0)){
      case ((_,i),a) if i == index => (Some(a),i)
      case ((_,i),_) => (None,i+1)
      case _ => (None,0)
    }._1

  def collectFirst[B](pf: PartialFunction[ASTNode,B]):Option[B] =
    foldLeft(Option.empty[B]){
      case (None,astNode) =>
        if (pf.isDefinedAt(astNode)) {
          Some(pf.apply(astNode))
        } else {
          None
        }
      case (s@Some(_),_) => s
      case _ => None
    }

  def find(f:ASTNode => Boolean):Option[ASTNode] =
    foldLeft(Option.empty[ASTNode]){
      case (None,astNode) if f(astNode) => Some(astNode)
      case (s@Some(_),_) => s
      case _ => None
    }

  def toList:List[ASTNode] =
    foldLeft(scala.collection.mutable.ListBuffer.empty[ASTNode]){
      (buf,a) => buf += a
    }.toList

  def filter(p:ASTNode => Boolean):List[ASTNode] =
    foldLeft(scala.collection.mutable.ListBuffer.empty[ASTNode]){
      (buf,a)  => if (p(a)) buf += a else buf
    }.toList


}
