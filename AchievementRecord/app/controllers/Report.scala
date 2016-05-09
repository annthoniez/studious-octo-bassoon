package controllers

import java.time.LocalDateTime
import java.util.UUID

import com.norbitltd.spoiwo.model._
import com.norbitltd.spoiwo.model.enums.CellFill
import com.norbitltd.spoiwo.natures.xlsx.Model2XlsxConversions._
import it.innove.play.pdf.PdfGenerator
import jp.t2v.lab.play2.auth.AuthElement
import models.Auth
import play.api.libs.Crypto
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Controller}
import views.html
/**
  * Created by Pichai Sivawat on 4/20/2016.
  */
trait Report extends Controller with Pjax with AuthElement with AuthConfigImpl {
  var pdfGenerator: PdfGenerator = new PdfGenerator

  def index() = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    Ok(views.html.report("สร้างรายงาน - ระบบกรอกข้อมูลผลงานต่างๆ ของนักศึกษา"))
  }

  def report() = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    val body: AnyContent = request.body
    val textBody: Option[Map[String, Seq[String]]] = body.asFormUrlEncoded

    val result = Search.getSearchResult(textBody)
    println(textBody)
    result.foreach(println)
    val saveFileName = Crypto.sign(UUID.randomUUID().toString + LocalDateTime.now().toString + loggedIn.username.value)

    val headerStyle =
      CellStyle(
        fillPattern = CellFill.Solid,
        fillForegroundColor = Color.LightGrey,
        font = Font(bold = true)
      )

    //TODO autoSized and date field
    val helloWorldSheet = Sheet(
      name = "Some serious stuff",
      rows = List(
        Row(style = headerStyle)
        .withCellValues("student_name",
          "ach_name",
          "t_accs",
          "orgs",
          "date",
          "category")) ++
        result.map(r =>
          Row()
            .withCellValues(r.accs.map(a => a.th_name).mkString(", "),
              r.achievement_name,
              r.t_accs.map(t => t.th_prename + t.th_name).mkString(", "),
              r.orgs.map(o => o.organization_name).mkString(", "),
              r.date.toString,
              r.category))
          .toList
    )
    helloWorldSheet.saveAsXlsx(play.Play.application().path().toString + s"/public/xlsxs/$saveFileName.xlsx")

    Ok(JsString(s"/assets/xlsxs/$saveFileName.xlsx"))
  }

  def pdf() = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    Ok(html.pdf(null))
  }

  def xls()  = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    val headerStyle =
      CellStyle(fillPattern = CellFill.Solid, fillForegroundColor = Color.AquaMarine, font = Font(bold = true))
    val helloWorldSheet = Sheet(name = "Some serious stuff")
      .withRows(
        Row(style = headerStyle).withCellValues("th_name", "ach_name", "t_name", "org", "date", "type"),
        Row().withCellValues("Marie Curie", 123, 66, true)
      )
      .withColumns(
        Column(index = 0, style = CellStyle(font = Font(bold = true)), autoSized = true)
      )
    helloWorldSheet.saveAsXlsx(play.Play.application().path().toString + "/public/xlsxs/hello_world.xlsx")
    Ok("test")
  }

  protected val main: User => Template = html.main.apply
}

object Report extends Report
