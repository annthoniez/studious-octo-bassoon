package controllers

import java.time.LocalDateTime
import java.util.UUID

import com.norbitltd.spoiwo.model.Height._
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

  def typeConvert(ach_type: Int) = {
    val result = ach_type match {
      case 1 => "Competition"
      case 2 => "Cert"
      case 3 => "Ambassador"
    }
    result
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

    val helloWorldSheet = Sheet(
      name = "Some serious stuff",
      rows = List(
        Row(style = headerStyle)
        .withCellValues("สมาชิก",
          "ชื่อผลงาน",
          "อาจารย์ที่ปรึกษา",
          "หน่วยงานที่จัด",
          "วันที่ได้รับ",
          "ประเภท",
          "รางวัล",
          "ชนิด")) ++
        result.map(r =>
          Row(height = 45 points)
            .withCellValues(r.accs.map(a => a.th_name).mkString("\n"),
              r.achievement_name,
              r.t_accs.map(t => t.th_prename + t.th_name).mkString("\n"),
              r.orgs.map(o => o.organization_name).mkString("\n"),
              r.date.toString,
              r.category,
              r.reward,
              typeConvert(r.achievement_type)))
          .toList
    ).withColumns(
      Column(index = 0, autoSized = true),
      Column(index = 1, autoSized = true),
      Column(index = 2, autoSized = true),
      Column(index = 3, autoSized = true),
      Column(index = 4, autoSized = true),
      Column(index = 5, autoSized = true),
      Column(index = 6, autoSized = true),
      Column(index = 7, autoSized = true)
    )
    helloWorldSheet.saveAsXlsx(play.Play.application().path().getCanonicalPath() + "/../webapps/" + s"/public/xlsxs/$saveFileName.xlsx")

    Ok(JsString(s"/public/xlsxs/$saveFileName.xlsx"))
  }

  def pdf() = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    Ok(html.pdf(null))
  }

  def xls()  = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    val cell = Cell("Use \n with word wrap on to create a new line", index = 2)
    val row = Row(index = 2, height = 45 points,cells = List(cell))
    val sheet = Sheet(row).withColumns(Column(2, autoSized = true))
    sheet.saveAsXlsx(play.Play.application().path().getCanonicalPath() + "/../webapps/" + "/public/xlsxs/ooxml-newlines.xlsx")
    Ok("test")
  }

  protected val main: User => Template = html.main.apply
}

object Report extends Report
