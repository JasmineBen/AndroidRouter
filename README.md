# AndroidRouter
A router library for Android App use annotation(@Route(authority="",path="",desc="")). Use this library, App can start an activity by Router.getInstance().openScheme(Context context, String url, Bundle data)


UserGuide:

step1:add following code in project's build.gradle
在project的build.gradle文件中添加如下依赖
maven {
  url  "https://dl.bintray.com/jasmineben/maven/"
}
dependencies {
  classpath 'com.conan.router:router-plugin:1.0.0'
}
  
step2: add following code in module's build.gradle
在模块的build.gradle中添加如下依赖
dependencies {
  implementation 'com.conan.router:router-anno:1.0.1'
  annotationProcessor 'com.conan.router:router-compiler:1.0.1'
  implementation 'com.conan.router:router-lib:1.0.1'
}

apply plugin: 'router.plugin'

router{
  routerImpl='com.conan.router.AppRouter'
}

step3:define Route

can only define on subclasss of Activity
定义Activity的路由，只能定义在Activity的子类
@Route(authority="sampleAuthority",path="samplePath",desc="sampleDesc")
public class xxxActivity extends AppCompatActivity{
}

this Route refer to URI: xxx://sampleAuthority/samplePath?...
路由格式和URI类似
  
step4:register Router

rigister Router implementation in your application,paramter must equals to 
注册没有模块的路由实现类，该类的全路径必须和routerImpl一致

router{
  routerImpl=""
}
for example:
Router.getInstance().addBaseRouterImpl("com.conan.router.AppRouter");

step5:multi-module support
if you apply AndroidRouter int multi-module project,each module must define different authority.
besides,yoou must register sub-module's Router implementation int main module,such as you can rigister this code in your main module:
如果在多module中使用这个路由，需要在主module中采用同样的方式注册子模块 路由实现
SampleApplicationLike.getInstance().onCreate();

step6:Inject params
if you want to send data to Activity,you can use @Inject(name="") annotation,inject params must define int your url.
如果要给目标Activity传如参数，可以通过@Inject方式进行注入，注意@Inject的name如果不为空，那么url中的参数必须一样，如果为空，url中的参数名需要和Inject定义的变量名一致

step7:start targetActivity
如下方式可以打开界面。
boolean result = Router.getInstance().openScheme(activity,url,bundleData,requestCode)
