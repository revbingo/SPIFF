# Generated by Buildr 1.3.5, change to your liking
# Version number for this release
VERSION_NUMBER = "0.1.0"
# Group identifier for your projects
GROUP = "com.revbingo"
COPYRIGHT = ""

JEL = file('lib/jel.jar')

# Specify Maven 2.0 remote repositories here, like this:
repositories.remote << "http://www.ibiblio.org/maven2/"

flat_layout = Layout.new
flat_layout[:source, :main, :java] = 'src'
flat_layout[:source, :test, :java] = 'test'


desc "The Spiff project"
define "SPIFF", :layout=>flat_layout do

  project.version = VERSION_NUMBER
  project.group = GROUP
  manifest["Implementation-Vendor"] = COPYRIGHT

  compile.with JEL
  package :jar
end

flat_layout = Layout.new
flat_layout[:source, :main, :java] = 'src'
flat_layout[:source, :test, :java] = 'test'