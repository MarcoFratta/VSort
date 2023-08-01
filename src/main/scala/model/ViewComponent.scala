package model

import com.raquo.laminar.api.L
import controller.Properties
import model.*
import org.scalajs.dom
import view.{BottomBar, GraphFunctions, MultipleListFactory, SingleValue, SingleValueFactory}
import com.raquo.laminar.api.L.{Element, button, canvasTag, child, children, className, div, i, li, nodeSeqToModifier, onClick, onLoad, onMountBind, onMountCallback, onMountInsert, render, renderOnDomContentLoaded, ul, windowEvents}
import com.raquo.laminar.api.eventPropToProcessor



object ViewComponent:
  trait View extends ModelTypes:
    def getAppElement: Element

    def update(data: ResultType): Unit
  trait Provider:
    val view: View with IntTypes

  type Requirements = ModelComponent.Model.Provider with ControllerComponent.Provider
  trait Component:
    c:Requirements =>

    class ViewImpl extends View with IntTypes:

      private var gui:JsView = JsView(Seq(), c.model.algorithms,c.model.distributions,
        Properties(c.model.algorithms.head, c.model.distributions.head,
          c.model.distributions.head.params.map(a => a -> 10).toMap))
      override def getAppElement: L.Element = gui.getAppElement

      override def update(data: c.model.ResultType): Unit = gui = gui.updated(data)
      private case class JsView(data:c.model.ResultType,
                                algorithms:Set[Algorithm[c.model.ValType,c.model.ResultType] with HasName],
                                distributions:Set[Distribution[c.model.ParamsType,c.model.ValType] with HasName],
                                p:Properties with IntTypes):
        import BottomBar.*

        println("Entering JsView")
        private val algo = MultipleListFactory(algorithms, p.algorithm)
        private val dis = MultipleListFactory(distributions, p.distribution)
        private var selectedP = p
        given Conversion[c.model.ParamsType, Int] = x => x
        private val params = p.params.map(a => SingleValueFactory(a._1, a._2)).toList

        dom.document.getElementById("app").innerHTML = ""
        render(dom.document.getElementById("app"), getAppElement)
        replaceView()

        private def replaceView(): Unit =
          dom.document.getElementById("app").addEventListener("onChange", _ =>  updated(data))

        def getAppElement: Element =
          println("getappelem")
          div(
            div(
              ul(className := "topBar",
                li(algo.element),
                li(dis.element),
                params.map(a => li(a.element)),
                li(
                  button(
                    i(
                      className := "fa fa-check",
                      onClick --> (_ =>
                        println("click")
                        val paramMap = params.map(a => a.get).foldLeft(Map.empty[Params, ParamsType])((param, map) => param ++ map)
                        println(f"new map after click $paramMap")
                        selectedP = Properties(algo.get.head._1, dis.get.head._1, paramMap)
                        c.controller.update(selectedP)
                       )
                    )
                  )
                )
              )
            ),
            onMountCallback (_ =>
              println("Callback")
              if data.nonEmpty then GraphFunctions(data)),
            div(canvasTag(
              className := "canvas")),
            div(className:= "bottomBar"),
          )

        def computeUl: Element =
          div(params.map(a => li(a.element)))

        def updated(data: c.model.ResultType): JsView = copy(data = data, p = selectedP)

  trait Interface extends Provider with Component:
    self: Requirements =>