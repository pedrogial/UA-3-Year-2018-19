using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Practica_2_SD
{
    public partial class Form1 : Form
    {
        Station.Station[] stations= new Station.Station[1];
        Station.Station st = new Station.Station();
        int temp, lum, hum;
        bool b;
        int id = 0;
        String registradas = "";

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        //Hace una consulta
        private void button2_Click(object sender, EventArgs e)
        {
            try{
                String a = comboBox1.Text;
                String c = a.Replace("Estacion ", "");
                int index = Convert.ToInt32(c);
                index--;
                Station.Station st = stations[index];
                switch (comboBox2.Text)
                {
                    case "Temperatura":
                        st.getTemp(out temp, out b);
                        richTextBox2.Text = "La temperatura de la estación " + "" + " es: " + temp.ToString() + "\n";
                        break;
                    case "Humedad":
                        st.getHum(out hum, out b);
                        richTextBox2.Text = "La humedad de la estación " + comboBox1.Text + " es: " + hum.ToString() + "\n";
                        break;
                    case "Luminosidad":
                        st.getLum(out lum, out b);
                        richTextBox2.Text = "La luminosidad de la estación " + comboBox1.Text + " es: " + lum.ToString() + "\n";
                        break;
                    case "LCD":
                        richTextBox2.Text = "El display de la estación " + comboBox1.Text + " es: " + st.getLCD() + "\n";
                        break;
                    case "Todo":
                        st.getHum(out hum, out b);
                        st.getLum(out lum, out b);
                        st.getTemp(out temp, out b);
                        richTextBox2.Text = "La temperatura de la estación " + comboBox1.Text + " es: " + temp.ToString() + "\n";
                        richTextBox2.Text += "La humedad de la estación " + comboBox1.Text + " es: " + hum.ToString() + "\n";
                        richTextBox2.Text += "La luminosidad de la estación " + comboBox1.Text + " es: " + lum.ToString() + "\n";
                        richTextBox2.Text += "El display de la estación " + comboBox1.Text + " es: " + st.getLCD();
                        break;
                    default:
                        richTextBox2.Text = "No se ha seleccionado ninguna consulta";
                        break;
                }
            
            }
            catch (Exception ex) {
                richTextBox2.Text = "Se ha producido un error: \n"+ ex;
            }
        }

        //Cambia el Display
        private void button3_Click(object sender, EventArgs e)
        {
            String a = comboBox1.Text;
            String c = a.Replace("Estacion ", "");
            int index = Convert.ToInt32(c);
            index--;
            Station.Station st = stations[index];

            String nombre = comboBox1.Text;
            String dis = richTextBox3.Text;
            st.setLCD(dis,out b,out b);
            richTextBox3.Clear();
        }

        //Registrar
        private void button1_Click(object sender, EventArgs e)
        {
            try{
                richTextBox1.Clear();
                if(!textBox1.Text.Contains(":")){
                    //UDDI
                    //Patron de búsqueda de servicios
                    Juddi.find_service servicio= new Juddi.find_service();
                    //service.generic="2.0";       ##En el caso de usar una version anterior a juddi v3
                    servicio.findQualifiers = new string[] { "approximateMatch" };
                    servicio.name = new Juddi.name[] { new Juddi.name()};
                    servicio.name[0].Value = "Estacion%";

                    //Inquire
                    Juddi.UDDIInquiryService inquire = new Juddi.UDDIInquiryService();
                    inquire.Url = "http://" + textBox1.Text + ":8081/juddiv3/services/inquiry";
                
                    //Lista de coincidencias
                    Juddi.serviceList lista;
                    lista = inquire.find_service(servicio);
                
                    //Construye el array de estaciones
                    int estacionesRegistradas = lista.serviceInfos.Length;

                    for (int i = 0; i < estacionesRegistradas;i++ ) {
                        Juddi.serviceInfo informacion = lista.serviceInfos[i];

                        Juddi.get_serviceDetail gsd = new Juddi.get_serviceDetail();
                        //gsd.generic="2.0"; ##En el caso de no usar juddi v3
                        gsd.serviceKey = new string[] { informacion.serviceKey };

                        Juddi.serviceDetail sd = new Juddi.serviceDetail();
                        sd = inquire.get_serviceDetail(gsd);

                        Juddi.businessService[] bs = sd.businessService;
                        Juddi.bindingTemplate[] bTemplate = bs[0].bindingTemplates;
                        Juddi.accessPoint AP = bTemplate[0].accessPoint;

                        //Nombre de la estacion
                        // richTextBox2.Text += informacion.name[0].Value +"\n";
                        //URL al WSDL : accessPoint.Value
                        // richTextBox2.Text += AP.Value + "\n";

                        Station.Station st = new Station.Station();
                        st.Url = AP.Value.ToString();
                        stations[id] = st;
                        //Obtener ip y puerto del servicio a consumir
                        String ip = AP.Value;
                        String[] aux2 = ip.Split(new char[] { '/'});
                        ip = aux2[2];
                        registradas += "Estacion " + (id + 1).ToString() + " --> " + ip+ "\n";
                        richTextBox1.Text = registradas;
                        id++;
                        comboBox1.Items.Add("Estacion " + id);
                        Station.Station[] stat = new Station.Station[id + 1];

                        //Copia el antiguo y lo mete en el nuevo
                        for (int j = 0; j < stations.Length; j++)
                        {
                            stat[j] = stations[j];
                        }
                        stations = new Station.Station[id + 1];
                        stations = stat;
                    }
                }

                else{
                    //NO UDDI
                    Station.Station st = new Station.Station();
                    String nuevaURL= "http://"+textBox1.Text + "/Station/services/Station.StationHttpSoap11Endpoint/";
                    st.Url = nuevaURL;
                    //Prueba de acceso, si la URL no permite acceder a un dato, no se registra
                    st.getLum(out lum, out b);
                    //Si da error al acceder, no registra
                    stations[id] = st;
                    registradas += "Estacion " + (id+1).ToString() +" --> "+ textBox1.Text+"\n";
                    richTextBox1.Text = registradas;
                    id++;
                    comboBox1.Items.Add("Estacion "+id);
                    Station.Station[] stat = new Station.Station[id+1];

                    //Copia el antiguo y lo mete en el nuevo
                    for (int i = 0; i < stations.Length; i++) {
                        stat[i] = stations[i];
                    }
                    stations= new Station.Station[id+1];
                    stations=stat;
                }
            }
            catch(Exception){
                richTextBox1.Text= "Se ha producido un error al registrar la nueva estación.";
            }
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

    }
}
