package model

import com.sun.tools.javac.util.Pair
import model.ElementInfo.*

import scala.annotation.tailrec

class StepsTransformer[T]:

  import model.Step.*
  private type State = Seq[ElementInfo[T]]

  def getResult(steps: Seq[Step], seq: Seq[T]): Seq[T] = this.applySteps(steps, seq.map(i => newInfo(i)))

  private def applySteps(steps: Seq[Step], seq: State): Seq[T] =
    steps.foldLeft(seq)((currentSeq, step) => getNewSeq(step, currentSeq)).map(e => e.value)

  private def getNewSeq(step: Step, seq: State): State = step match
    case Step.Comparison(_, _) => seq
    case s => stepFunction(s)(seq)

  private def stepFunction(step: Step): State => State = step match
    case Step.Swap(a: Int, b: Int) => seq
      => seq.updated(a, seq(a).changeValue(seq(b).value)).updated(b, seq(b).changeValue(seq(a).value))
    case Step.Selection(s: String, a: Int) => seq => stepFunction(Step.Deselection(s))(seq) match
        case n if a >= 0 && a < n.size => n.updated(a, n(a).select(s))
        case n => n
    case Step.Deselection(s) => seq
      => seq.map(e => if e.label == Option(s) then deselect(e) else e)
    case Step.Comparison(a: Int, b: Int) => seq
      => seq.updated(a, compare(seq(a))).updated(b, compare(seq(b)))
    case Step.Divide(a: Int, b: Int) => seq
      => seq.zipWithIndex.map((e, i) => if i < a || i > b then hide(e) else show(e))

  def getString(steps: Seq[Step], seq: Seq[T]): String =

    this.getStepsString(steps, seq.map(i => newInfo(i)))

  private def getStepsString(steps: Seq[Step], map: State): String =
    steps.foldLeft("Array finale -> " + map) ((acc, step) =>
      val seqString = getSeq(step, map).toString
      acc + s" -> $seqString\n" + getNewSeq(step, map))

  private def getSeq(step: Step, map: State): State = stepFunction(step)(map)

  def getSeqList(steps: Seq[Step], seq: Seq[T]): Seq[State] =
    getSeqList(steps, seq.map(i => newInfo(i)), Seq(seq.map(i => newInfo(i))))
      .sliding(2).flatMap {
        case Seq(a, b) if a == b => None
        case Seq(a, _) => Some(a)}.toSeq

  private def getSeqList(steps: Seq[Step], last: State, seq: Seq[State]): Seq[State] =
    steps.foldLeft(Seq(last)) ((acc, step) => acc :+ getNewSeq(step, acc.last)).tail


