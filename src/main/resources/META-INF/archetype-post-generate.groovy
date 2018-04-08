def sep = File.separator
def moduleDir = new File(request.getOutputDirectory() + sep + request.getArtifactId())

// make script file be executable
moduleDir.eachFileMatch(~/run_.*/) { file ->
    file.setExecutable(true, false)
}


// replace secret placeholder with random generated secret
def srcDir = new File(moduleDir, "src")

def replacePatternInFile(file, Closure replaceText) {
    file.write(replaceText(file.text))
}

srcDir.eachDirRecurse() { dir ->
    dir.eachFileMatch(~/.*properties/) { file ->
        replacePatternInFile(file) {
            Random random = new Random(System.currentTimeMillis())
            def pool = ['a'..'z','A'..'Z',0..9].flatten()
            key = (1..64).collect { pool[random.nextInt(pool.size())] }
            it.replaceAll("__act-secret__", key.join())
        }
    }
}
