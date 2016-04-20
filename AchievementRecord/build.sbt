import com.github.play2war.plugin.{Play2WarKeys, Play2WarPlugin}

name := "AchievementRecord"

version := "1.0"

lazy val `achievementrecord` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( cache , ws   , specs2 % Test )

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "1.1.1"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1"

libraryDependencies ++= Seq("jp.t2v" %% "play2-auth"        % "0.14.2",
  "jp.t2v" %% "play2-auth-social" % "0.14.2", // for social login
  "jp.t2v" %% "play2-auth-test"   % "0.14.2" % "test")

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"        % "2.3.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.3+",
  "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "2.3+",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3+",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.5.0",
  "org.scalikejdbc" %% "scalikejdbc-jsr310" % "2.3.5",
  "org.skinny-framework" %% "skinny-orm"      % "2.0.8",
  "com.h2database"       %  "h2"              % "1.4.+",
  "ch.qos.logback"       %  "logback-classic" % "1.1.+",
  "ch.qos.logback"  % "logback-core" % "1.1+"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"
