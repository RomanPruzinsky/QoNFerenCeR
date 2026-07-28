# Keep release stack traces readable
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Jackson binds by name and reads generics, annotations and Kotlin metadata reflectively
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
-keep class kotlin.Metadata { *; }
-dontwarn com.fasterxml.jackson.databind.**

# Wire contract: renaming these fields silently breaks every request and response
-keep class tr.qonferencer.shared.dtos.** { *; }
-keep class tr.qonferencer.shared.enums.** { *; }
