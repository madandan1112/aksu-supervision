import subprocess, sys, os
subprocess.Popen(
    [r"D:\aksu-supervision\dev-env\jdk-17\jdk-17.0.12+7\bin\java.exe", "-jar", r"D:\aksu-supervision\backend\target\aksu-supervision-backend-1.0.0.jar"],
    stdout=open(r"D:\aksu-supervision\backend.log", "w"),
    stderr=open(r"D:\aksu-supervision\backend-err.log", "w"),
    creationflags=subprocess.CREATE_NEW_PROCESS_GROUP | subprocess.DETACHED_PROCESS,
    close_fds=True
)
print("Backend started")
