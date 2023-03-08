package it.rialtlas.healthmonitor.view;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.model.PatientData;
import it.rialtlas.healthmonitor.utils.MessagingUtils;
import it.rialtlas.healthmonitor.utils.UpdatePatientsTask;

public class PatientSelectorFragment extends Fragment {

    ArrayAdapter<PatientData> patientsListViewAdapter;
    private List<PatientData> patientsListViewArray =  new ArrayList<PatientData>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_selector_fragment, container, false);

        View.OnClickListener selectPatientOnClickListener = new View.OnClickListener() {
            private void openSelectionDialog() {
                ImageView ivStartMeasuring = ((ImageView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.ivStartMeasuringEnabled));
                if (ivStartMeasuring != null) {
                    ivStartMeasuring.setVisibility(View.VISIBLE);
                }
                // custom dialog
                final Dialog dialog = new Dialog(MeasurementsContextStrategy.getInstance().currentActivity());
                dialog.setContentView(R.layout.select_user_dialog);
                dialog.setTitle("Selezionare il paziente...");

                // set the custom dialog components - text, image and button
                patientsListViewAdapter = new ArrayAdapter<PatientData>(getContext(), R.layout.patients_list_row_layout, patientsListViewArray);
                final ListView patientsListViewItems = (ListView) dialog.findViewById(R.id.lvPatients);
                patientsListViewItems.setAdapter(patientsListViewAdapter);

                patientsListViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                        //get id of the selected item using position 'i'
                        view.setSelected(true);
                        MeasurementsContextStrategy.getInstance().patientId((PatientData) parent.getItemAtPosition(position));
                        ((Button) dialog.findViewById(R.id.btnOK)).setEnabled(true);
                    }
                });
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        resetPatientsData();
                        // get current users
                        new UpdatePatientsTask(patientsListViewItems, patientsListViewArray,
                                (LinearLayout)dialog.findViewById(R.id.llProgress),
                                (Button) dialog.findViewById(R.id.btnOK), (Button) dialog.findViewById(R.id.btnCancel)).execute(null, null, null);
                    }

                });

                Button okButton = (Button) dialog.findViewById(R.id.btnOK);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PatientData selectedPatient = MeasurementsContextStrategy.getInstance().selectedPatient();
                        if (MeasurementsContextStrategy.getInstance().currentActivity()!= null) {
                            MeasurementsContextStrategy.getInstance().currentActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.selectedPatient)).setText(selectedPatient.toString());
                                    ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.wristData)).setText(selectedPatient.getWrist());
                                }
                            });
                            MeasurementsContextStrategy.getInstance().state().scan(MeasurementsContextStrategy.getInstance());
                        }
                        if (selectedPatient==PatientData.NULL) {
                            MessagingUtils.oKDialog(R.string.MSG_WARNING, "Nessun paziente selezionato!", null);
                        }
                        dialog.dismiss();
                    }
                });

                Button cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MeasurementsContextStrategy.getInstance().patientId(PatientData.NULL);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
            @Override
            public void onClick(View v) {
                // new UpdatePatientsTask().execute(null, null, null);
                if (MeasurementsContextStrategy.getInstance().state() == HeloLXPlusControllerState.MUST_SEND) {
                    MessagingUtils.oKCancelDialog(R.string.MSG_WARNING, "Attenzione! Sono presenti misure non trasmesse.\nSi desidera annullare i dati correnti?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openSelectionDialog();
                                }
                            },
                            null
                    );
                    return;
                }
                openSelectionDialog();
            }
        };

        TextView selectedPatient =  (TextView) view.findViewById(R.id.selectedPatient);
        selectedPatient.setText(PatientData.NULL.getName());
        selectedPatient.setOnClickListener(selectPatientOnClickListener);

        TextView patientLabel =  (TextView) view.findViewById(R.id.patientLabel);
        patientLabel.setOnClickListener(selectPatientOnClickListener);

        ((TextView) view.findViewById(R.id.wristData)).setText("");
        return view;
    }

    private void resetPatientsData() {
        MeasurementsContextStrategy.getInstance().patientId(PatientData.NULL);
        MeasurementsContextStrategy.getInstance().state().unbind(MeasurementsContextStrategy.getInstance());

        // azzera tutti i dati
        MeasurementsContextStrategy.getInstance().systolicPressure(null);
        MeasurementsContextStrategy.getInstance().diastolicPressure(null);
        MeasurementsContextStrategy.getInstance().heartRate(null);
        MeasurementsContextStrategy.getInstance().breathRate(null);
        MeasurementsContextStrategy.getInstance().spo2(null);
        MeasurementsContextStrategy.getInstance().steps(null);
        MeasurementsContextStrategy.getInstance().mood(null);
        MeasurementsContextStrategy.getInstance().fatigue(null);
        MeasurementsContextStrategy.getInstance().glucose(null);
        MeasurementsContextStrategy.getInstance().weight(null);
        MeasurementsContextStrategy.getInstance().currentActivity().runOnUiThread(new Runnable() {
            public void run() {
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.selectedPatient)).setText(PatientData.NULL.getName());
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.wristData)).setText("");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.systolic)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.diastolic)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.heartRate)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.spo2)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.glucose)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.weight)).setText("--");
            }
        });
    }

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pascale01";
}
