' =========================================================
' minired.red — Red mínima (sin errores)
' Ejemplo 2 del proyecto de compiladores
' =========================================================

red MiniRed {

    router R1 {
        interfaz g0/0 ip 10.0.0.1/30;
    }

    firewall FW1 {
        interfaz outside ip 10.0.0.2/30;
        interfaz inside  ip 192.168.1.1/24;
    }

    switch SW1 {
        interfaz f0/1;
        interfaz f0/2;
    }

    pc PC1 {
        interfaz eth0 ip 192.168.1.10/24;
    }

    internet NET {
        interfaz wan0;
    }

    conectar PC1.eth0    -> SW1.f0/1;
    conectar SW1.f0/2    -> FW1.inside;
    conectar FW1.outside -> R1.g0/0;
    conectar R1.g0/0     -> NET.wan0;
}
