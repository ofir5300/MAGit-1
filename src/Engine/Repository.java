package Engine;

import java.nio.file.Path;
import java.util.HashSet;

public class Repository {

    private Path rootPath;
    private Branch HEAD;                    // pointer to active branch
    private HashSet<Branch> branches;

    public Repository(Path rootPath, Branch head) {
        this.rootPath = rootPath;
        this.HEAD = head;
        branches = new HashSet<Branch>();
        branches.add(HEAD);
    }

    public Branch getHEAD() {
        return this.HEAD;
    }

    public Repository(Path rootPath, Branch head, HashSet<Branch> branches) {
        new Repository(rootPath, head);
        this.branches = branches;
    }

    public void swichHEAD(Branch newHead)
    {
        this.HEAD = newHead;
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public HashSet<Branch> getBranches () {
        return this.branches;
    }
}
