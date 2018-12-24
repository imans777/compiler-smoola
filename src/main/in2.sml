class Main {
    def main() : int {
        return 0;
    }
}


class d {
    def b2() : string {
        return "Salam";
    }
}

class c extends d{
    def a() : int {
        return 0;
    }
}

class a extends c{
    var x : int[];
    def b(c : int, e : d) : int {
        return c - this.f()[5];
    }
    def f() : int[] {
        var t: int;
        var t2: int;
        t = 0;
        t2 = 0;
        while (t <> 5) {
            t = t + 1;
        }
        if (t > 4 && t < 6 || t == 5) then
            t2 = 9;
        else
            t2 = -5;
        return x;
    }
}
