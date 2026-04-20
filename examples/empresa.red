' =========================================================
' empresa.red — Red corporativa completa (sin errores)
' Ejemplo 1 del proyecto de compiladores
' =========================================================

red Empresa {

    ' ── Dispositivos de borde ────────────────────────────
    router R1 {
        interfaz g0/0 ip 10.0.0.1/30;
        interfaz g0/1 ip 192.168.1.1/24;
    }

    firewall FW1 {
        interfaz inside  ip 192.168.1.254/24;
        interfaz outside ip 10.0.0.2/30;
    }

    ' ── Núcleo de la red ─────────────────────────────────
    switchL3 CORE {
        interfaz g1/0/1;
        interfaz g1/0/2;
        interfaz vlan10 ip 192.168.10.1/24;
        interfaz vlan20 ip 192.168.20.1/24;
    }

    switch SW1 {
        interfaz f0/1;
        interfaz f0/2;
        interfaz f0/3;
    }

    ' ── Servidores ───────────────────────────────────────
    server WEB1 tipo web {
        interfaz eth0 ip 192.168.10.10/24;
    }

    server DB1 tipo database {
        interfaz eth0 ip 192.168.20.10/24;
    }

    ' ── Equipos de usuario ───────────────────────────────
    pc PC1 {
        interfaz eth0 ip 192.168.10.20/24;
    }

    pc PC2 {
        interfaz eth0 ip 192.168.20.21/24;
    }

    laptop L1 {
        interfaz wlan0 ip 192.168.10.30/24;
    }

    mobile M1 {
        interfaz wifi0 ip 192.168.10.40/24;
    }

    ' ── Acceso inalámbrico ───────────────────────────────
    accesspoint AP1 {
        interfaz wlan1;
        interfaz wlan2;
        interfaz eth0;
    }

    ' ── Salida a internet ────────────────────────────────
    internet NET {
        interfaz wan0;
    }

    ' ── Conexiones ───────────────────────────────────────
    conectar PC1.eth0    -> SW1.f0/1;
    conectar PC2.eth0    -> SW1.f0/2;
    conectar L1.wlan0    -> AP1.wlan1;
    conectar M1.wifi0    -> AP1.wlan2;
    conectar AP1.eth0    -> SW1.f0/3;
    conectar WEB1.eth0   -> CORE.g1/0/1;
    conectar DB1.eth0    -> CORE.g1/0/2;
    conectar CORE.vlan10 -> FW1.inside;
    conectar FW1.outside -> R1.g0/0;
    conectar R1.g0/1     -> NET.wan0;
}
