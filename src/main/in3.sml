class Main {
    def main() : int {
        writeln(new ed().func(3));
        return 2;
    }
}

class d {
    var arr: int[];
    def b() : int {
        arr = new int[4];
        return arr.length;
    }
}

class ed extends d {
    var e: int;
    def func(sd: int): int {
        e = (1 + 2 * 3 + 5) / 6;
        writeln(e);
        if (e <> 1) then
            while (e <> 5) {
                e = e + 1;
            }
        else
            e = 5;
        return -e - this.b() + sd;
    }
}