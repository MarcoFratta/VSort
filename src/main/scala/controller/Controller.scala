package controller

import controller.StepController.{SeqProp, SeqProperties, example}
import model.{Algorithm, Distribution, ElementInfo, HasName, InputType, IntModelImpl, Model, Params, State}
import view.{GraphFunctions, TopBar, View, ViewImpl}
trait Controller:
  //def getInputList: List[InputType]
  //def getVariableInputList: List[InputType]
  //def getElements: Seq[Seq[ElementInfo[Int]]]
  //def setSeqSize(size: Int): GraphFunctions
  //def addProperties(name: String, value: Int): Properties
  def setProperties(prop: Properties): Unit
  def getAlgorithms: List[Algorithm[Int, Seq[State[ElementInfo[Int]]]] with HasName ]
  def getDistribution: List[Distribution[Int, Int] with HasName]
  //def sendData(): GraphFunctions
  //def onOptionSelected(field: String, value: String): Unit



trait Properties:
  def alg: Algorithm[Int, Seq[State[ElementInfo[Int]]]]
  def alg_=(algorithm: Algorithm[Int, Seq[State[ElementInfo[Int]]]]): Unit
  def distribution: Distribution[Int, Int]
  def distribution_=(distribution: Distribution[Int, Int]): Unit
  def map: Map[Params, Int]
  def map_=(map: Map[Params, Int]): Unit
case class PropertiesImpl(override var alg: Algorithm[Int, Seq[State[ElementInfo[Int]]]],
                      override var distribution: Distribution[Int, Int],
                      override var map: Map[Params, Int]) extends Properties
class ControllerImpl() extends Controller:
  val view: View = new ViewImpl(this)

  override def getAlgorithms: List[Algorithm[Int, Seq[State[ElementInfo[Int]]]] with HasName]= IntModelImpl().algorithms.toList

  override def getDistribution: List[Distribution[Int, Int] with HasName] = IntModelImpl().distributions.toList

  override def setProperties(prop: Properties): Unit =
    if checkField(prop)
      then sendData(prop)
 /* override def getElements: Seq[Seq[ElementInfo[Int]]] =
    println("elements" )
    SeqProperties(properties).getElements

  override def setSeqSize(size: Int): GraphFunctions =
    properties.map = properties.map.updated(findParamFromName("Size"), size)
    view.setSeqList(SeqProperties(properties).getElements)


  override def addProperties(name: String, value: Int): Map[Params, Int] =
    properties.map = properties.map.updated(findParamFromName(name), value)
    println("aggiornata mappa: "+ properties.map)
    if checkField() then sendData()
    properties.map

  private def findParamFromName(name: String): Params =
    properties.map.filter(a => a._1.toString equals name).toList.head._1


  */
  def sendData(properties: Properties): GraphFunctions =
    view.setSeqList(SeqProperties(properties).getElements)
/*
  override def onOptionSelected(field: String, value: String): Unit =
    field match
      case "Algorithm" => properties.alg = IntModelImpl().algorithms.filter(a => a.name eq(value)).toList.head
      case "Distribution" =>
        properties.distribution = IntModelImpl().distributions.filter(a => a.name eq(value)).toList.head
        properties.map = defaultMap
        properties.distribution.params.foreach(a => properties.map = properties.map.updated(a, -1))
        println("distribution")
        properties.distribution.params.foreach(a => println(a.toString))
        properties.map.foreach(a => println(a._1.toString))
        TopBar(this).replaceTopBar(computeVariableInputList(properties.distribution))
      case _ =>
    if checkField() then sendData()

*/

  private def checkField(properties: Properties): Boolean =
    properties.alg != null && properties.distribution != null && properties.map.nonEmpty &&
      properties.map.filter(a => a._2 < 0).toList.isEmpty