package support

import scala.reflect.macros.blackbox.Context

class HandsOnMacro[C <: Context](val c: C) {
  import c.universe._

  def apply(testName: c.Expr[String])(testFun: c.Expr[Unit])(suite: c.Expr[HandsOnSuite]): c.Expr[Unit] = {
    val (start, end) = getLines(testFun.tree)
    val content = testFun.tree.pos.source.content.mkString

    c.Expr(q"""support.HandsOnSuite.runTest($testName, $suite)($testFun)(new support.TestContext($content, $start, $end))""")
  }

  private def getLines(tree: Tree): (Int, Int) =
    tree match {
      case Block(xs, y) => (tree.pos.line, y.pos.line)
      case _ => (tree.pos.line, tree.pos.line)
    }
}

object HandsOnMacro {
  def apply(c: Context)(testName: c.Expr[String])(testFun: c.Expr[Unit])(suite: c.Expr[HandsOnSuite]): c.Expr[Unit] = {
    new HandsOnMacro[c.type](c).apply(testName)(testFun)(suite)
  }
}
