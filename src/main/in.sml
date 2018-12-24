class Main {
    def main() : int {
        new d().b();
        writeln(new wer().func("hi"));
        return 2;
    }
}

class d {
    var arr: int[];
    var myb: int;

    def b() : int {
        arr = new int[4];
        arr[0] = 2;
        myb2[0] = 2;
        return arr.length;
    }

    def another() : int {
        writeln(this.b());
        return myb;
    }
}

class wer extends d {
    var e: int;
    var tempok: string;
    def func(sd: int) : int {
        var e: int;
        e = (1 + 2 * 3 + 5) / 6;
        tempok = "ok";
        writeln(tempok);
        if (e <> 1) then
            while (e <> 5) {
                e = e + 1;
            }
        else
            e = 5;
        return -e - this.b() + sd;
    }
}

class c2 extends c1 {

}

class c1 extends c2 {

}

class c3 {
    var a : int;

    def test() : int {
        var b: d;
        b = new d();
        return 2;
    }
}