package controllers

import java.io.{BufferedOutputStream, FileOutputStream}
import java.time.LocalDateTime
import java.util.UUID
import scala.collection.JavaConversions._

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
    val bos = new BufferedOutputStream(new FileOutputStream(play.Play.application().path().toString + s"/public/pdfs/$saveFileName.pdf"))
    val list = seqAsJavaList(Seq("fonts/THSarabunNew.ttf"))

    pdfGenerator.loadLocalFonts(list)
    Stream.continually(bos.write(pdfGenerator.toBytes(html.pdf(result), "http://localhost:9000")))
    bos.close()
    Ok(JsString(s"/assets/pdfs/$saveFileName.pdf"))
  }

  def pdf() = StackAction(AuthorityKey -> Seq(Auth.Staff)) { implicit request =>
    Ok(html.pdf(null))
  }

  protected val main: User => Template = html.main.apply
}

object Report extends Report
