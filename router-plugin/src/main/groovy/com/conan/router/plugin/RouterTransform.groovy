package com.conan.router.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

public class RouterTransform extends Transform {

    Project mProject

    public RouterTransform(Project project) {
        mProject = project
    }

    @Override
    public String getName() {
        return 'RouterTransform'
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return  TransformManager.PROJECT_ONLY
    }

    @Override
    public boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        //对类型为“文件夹”的input进行遍历
        List<String> routerParserImpl = new ArrayList<>()
        inputs.each {
            TransformInput input ->
                input.jarInputs.each {
                    JarInput jarInput ->
                        MyInject.injectJar(jarInput.file.absolutePath)
                        String outputFileName = jarInput.name.replace(".jar", "") + '-' + jarInput.file.path.hashCode()
                        def output = outputProvider.getContentLocation(outputFileName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        FileUtils.copyFile(jarInput.file, output)
                }

                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        routerParserImpl.addAll(MyInject.collectRouterParserImpl(directoryInput.file.absolutePath))
                        MyInject.injectDir(directoryInput.file.absolutePath, mProject, routerParserImpl)
                        // 获取output目录
                        def dest = outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes, directoryInput.scopes,
                                Format.DIRECTORY)
                        // 将input的目录复制到output指定目录
                        FileUtils.copyDirectory(directoryInput.file, dest)
                }

        }
        //关闭classPath，否则会一直存在引用
        MyInject.removeClassPath(mProject)
    }


}
