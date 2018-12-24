class Main {
    def main() : int {
        new d().func2(true);
        this.toString();
        return 2;
    }
}


class d extends e {
    var arr: int[];
    var i: int;
    var str: string;
    var di: d;
    var ei: e;
    var bo: boolean;

    def func2(zzz: boolean) : boolean {
        ei = new e();
        i = ei.master();
        writeln(this.toString());
        return !bo;
    }

    def b() : int {
        arr = new int[4];
        i = 2 + 3 * arr.length;
        i = new d().func(-arr.length + arr[i] + 1 / i);
        bo = new d().func2(!bo && false || bo);
        ei = new e();
        i = ei.ok(2);
        ei = new d();
        bo = ei.func2(true);
        ei = new e();
        bo = ei.func2(true);
        i = di.master();
        i = this.ok(2);
        if (this.func(2) == 1 + 3) then
          str = "hi";
        #this.toString(); -> still error!
        return arr.length;
    }

    def func(zzz: int) : int {
      return 2;
    }
}

class part extends part2 {
}

class part2 extends part {

}

class f {
    def ok(i: int) : int {
        return (i + 2);
    }
}

class e extends f {
    def master() : int {
        return 2;
    }
}


