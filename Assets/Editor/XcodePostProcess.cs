#if UNITY_EDITOR && UNITY_IOS
using System.IO;
using UnityEditor;
using UnityEditor.Callbacks;
using UnityEditor.iOS.Xcode;

sealed class XcodePostProcess
{
    [PostProcessBuild]
    static void OnPostProcessBuild(BuildTarget target, string path)
    {
        if (target != BuildTarget.iOS) return;

        var project = new PBXProject();
        var projectPath = PBXProject.GetPBXProjectPath(path);
        var projectText = File.ReadAllText(projectPath);
        project.ReadFromString(projectText);

        var mainTargetGuid = project.GetUnityMainTargetGuid();
        var frameworkTargetGuid = project.GetUnityFrameworkTargetGuid();

        // ImportFilesで新規コピーするファイルをXcodeプロジェクトにも追加する
        var sampleNotifyWavGuid = project.AddFile(
            path: "sample_notify.wav",
            projectPath: "sample_notify.wav",
            sourceTree: PBXSourceTree.Source);

        // Copy Bundle Resoucesにも追加（追加しないと実行時にファイルが見つからないエラーが出る）
        var resourcesBuildPhaseGuid = project.GetResourcesBuildPhaseByTarget(mainTargetGuid);
        project.AddFileToBuildSection(mainTargetGuid, resourcesBuildPhaseGuid, sampleNotifyWavGuid);

        File.WriteAllText(projectPath, project.WriteToString());

        var sourceFile = Path.Combine("Assets", "Plugins", "Android", "Sample.androidlib", "res", "raw", "sample_notify.wav");
        var destinationFile = Path.Combine(path, "sample_notify.wav");
        File.Copy(sourceFile, destinationFile);
    }
}
#endif
