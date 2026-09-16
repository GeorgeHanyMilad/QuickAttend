To honour the JVM settings for this build a single-use Daemon process will be forked. For more on this, please refer to https://docs.gradle.org/8.4/userguide/gradle_daemon.html#sec:disabling_the_daemon in the Gradle documentation.
Daemon will be stopped at the end of the build 
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:mergeDebugNativeDebugMetadata NO-SOURCE
> Task :app:checkKotlinGradlePluginConfigurationErrors
> Task :app:generateDebugResValues
> Task :app:checkDebugAarMetadata
> Task :app:mapDebugSourceSetPaths
> Task :app:generateDebugResources
> Task :app:packageDebugResources
> Task :app:createDebugCompatibleScreenManifests
> Task :app:extractDeepLinksDebug
> Task :app:parseDebugLocalResources
> Task :app:mergeDebugResources
> Task :app:processDebugMainManifest
> Task :app:processDebugManifest
> Task :app:javaPreCompileDebug
> Task :app:mergeDebugShaders
> Task :app:compileDebugShaders NO-SOURCE
> Task :app:generateDebugAssets UP-TO-DATE
> Task :app:mergeDebugAssets
> Task :app:compressDebugAssets
> Task :app:desugarDebugFileDependencies
> Task :app:mergeDebugJniLibFolders
> Task :app:mergeDebugNativeLibs NO-SOURCE
> Task :app:checkDebugDuplicateClasses
> Task :app:processDebugManifestForPackage
> Task :app:stripDebugDebugSymbols NO-SOURCE
> Task :app:mergeLibDexDebug
> Task :app:processDebugResources
> Task :app:validateSigningDebug
> Task :app:writeDebugAppMetadata
> Task :app:writeDebugSigningConfigVersions
> Task :app:buildKotlinToolingMetadata
> Task :app:preReleaseBuild UP-TO-DATE
> Task :app:generateReleaseResValues
> Task :app:checkReleaseAarMetadata
> Task :app:mapReleaseSourceSetPaths
> Task :app:generateReleaseResources
> Task :app:packageReleaseResources
> Task :app:parseReleaseLocalResources
> Task :app:createReleaseCompatibleScreenManifests
> Task :app:extractDeepLinksRelease
> Task :app:mergeReleaseResources
> Task :app:processReleaseMainManifest
> Task :app:processReleaseManifest
> Task :app:processReleaseManifestForPackage
> Task :app:javaPreCompileRelease
> Task :app:extractProguardFiles
> Task :app:mergeReleaseJniLibFolders
> Task :app:mergeReleaseNativeLibs NO-SOURCE
> Task :app:stripReleaseDebugSymbols NO-SOURCE
> Task :app:extractReleaseNativeSymbolTables NO-SOURCE
> Task :app:mergeReleaseNativeDebugMetadata NO-SOURCE
> Task :app:processReleaseResources
> Task :app:checkReleaseDuplicateClasses
> Task :app:desugarReleaseFileDependencies
> Task :app:kspDebugKotlin
> Task :app:kspReleaseKotlin
> Task :app:mergeExtDexDebug
> Task :app:mergeReleaseArtProfile
> Task :app:mergeReleaseShaders
> Task :app:compileReleaseShaders NO-SOURCE
> Task :app:generateReleaseAssets UP-TO-DATE
> Task :app:mergeReleaseAssets
> Task :app:compressReleaseAssets
> Task :app:mergeExtDexRelease
> Task :app:collectReleaseDependencies
> Task :app:sdkReleaseDependencyData
> Task :app:writeReleaseAppMetadata
> Task :app:writeReleaseSigningConfigVersions
> Task :app:optimizeReleaseResources
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:26:46 Unresolved reference: SummaryScreen

e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:82:17 Unresolved reference: SummaryScreen
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:90:17 Overload resolution ambiguity: 

> Task :app:compileReleaseKotlin FAILED
public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt
public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt
gradle/actions: Writing build results to /home/runner/work/_temp/.gradle-actions/build-results/__run-1789542511983.json
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:94:44 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:94:55 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/AttendanceScreen.kt:65:1 Conflicting overloads: public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt, public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/CreateSessionDialog.kt:660:13 Cannot find a parameter with this name: shape
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/CreateSessionDialog.kt:661:13 Cannot find a parameter with this name: containerColor
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/SummaryScreen.kt:65:1 Conflicting overloads: public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt, public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:26:46 Unresolved reference: SummaryScreen
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:82:17 Unresolved reference: SummaryScreen
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:90:17 Overload resolution ambiguity: 
public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt
public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:94:44 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/MainActivity.kt:94:55 Cannot infer a type for this parameter. Please specify it explicitly.
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/AttendanceScreen.kt:65:1 Conflicting overloads: public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt, public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/CreateSessionDialog.kt:660:13 Cannot find a parameter with this name: shape
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/CreateSessionDialog.kt:661:13 Cannot find a parameter with this name: containerColor
e: file:///home/runner/work/QuickAttend/QuickAttend/app/src/main/java/com/georgehany/quickattend/ui/screens/SummaryScreen.kt:65:1 Conflicting overloads: public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file AttendanceScreen.kt, public fun AttendanceScreen(session: Session, students: List<Student>, records: Map<String, AttendanceRecord>, onRecordAttendance: (studentId: String, isPresent: Boolean) -> Unit, onUndo: () -> Unit, onBack: () -> Unit): Unit defined in com.georgehany.quickattend.ui.screens in file SummaryScreen.kt

FAILURE: Build completed with 2 failures.
57 actionable tasks: 57 executed

1: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

2: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:compileReleaseKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

BUILD FAILED in 54s
Error: Process completed with exit code 1.
