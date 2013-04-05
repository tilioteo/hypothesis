/**
 * 
 */
package org.hypothesis.application.junit;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class SlideConstants {

	public static final String TEMPLATE_XML = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideTemplate UID=\"B80BF9AC-C488-41F7-AB95-52BB7EAB9F50\">" +
"	<Viewport>" +
"		<VerticalLayout Id=\"ct001\">" +
"			<Properties>" +
"				<Width Value=\"100%\" />" +
"				<Height Value=\"100%\" />" +
"			</Properties>" +
"			<Components>" +
"				<HorizontalLayout Id=\"ct001_ct001\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"10%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<Panel Id=\"ct001_ct001_ct001\">" +
"							<Properties>" +
"								<Border Value=\"True\" />" +
"								<Width Value=\"80%\" />" +
"							</Properties>" +
"						</Panel>" +
"					</Components>" +
"				</HorizontalLayout>" +
"				<HorizontalLayout Id=\"ct001_ct002\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"30%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<FormPanel Id=\"ct001_ct002_ct001\">" +
"							<Properties>" +
"								<Border Value=\"True\" />" +
"								<Width Value=\"80%\" />" +
"							</Properties>" +
"							<Components>" +
"								<TextArea Id=\"6\">" +
"									<Properties>" +
"										<Width Value=\"99%\" />" +
"									</Properties>" +
"								</TextArea>" +
"							</Components>" +
"						</FormPanel>" +
"					</Components>" +
"				</HorizontalLayout>" +
"				<HorizontalLayout Id=\"ct001_ct003\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"10%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<Panel Id=\"ct001_ct003_ct001\">" +
"							<Properties>" +
"								<Border Value=\"True\" />" +
"								<Width Value=\"80%\" />" +
"							</Properties>" +
"						</Panel>" +
"					</Components>" +
"				</HorizontalLayout>" +
"				<HorizontalLayout Id=\"ct001_ct004\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"30%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<FormPanel Id=\"ct001_ct004_ct001\">" +
"							<Properties>" +
"								<Border Value=\"True\" />" +
"								<Width Value=\"80%\" />" +
"							</Properties>" +
"							<Components>" +
"								<TextArea Id=\"11\">" +
"									<Properties>" +
"										<Width Value=\"50%\" />" +
"									</Properties>" +
"								</TextArea>" +
"							</Components>" +
"						</FormPanel>" +
"					</Components>" +
"				</HorizontalLayout>" +
"				<HorizontalLayout Id=\"ct001_ct005\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"20%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<HorizontalLayout Id=\"ct001_ct005_ct001\">" +
"							<Properties>" +
"								<Width Value=\"30%\" />" +
"								<Height Value=\"100%\" />" +
"							</Properties>" +
"							<Components>" +
"								<Button Id=\"ct001_ct005_ct001_ct001\">" +
"									<Properties>" +
"									</Properties>" +
"									<Handlers>" +
"										<Click Action=\"prictipocitadlo\" />" +
"									</Handlers>" +
"								</Button>" +
"							</Components>" +
"						</HorizontalLayout>" +
"						<HorizontalLayout Id=\"ct001_ct005_ct002\">" +
"							<Properties>" +
"								<Width Value=\"40%\" />" +
"								<Height Value=\"100%\" />" +
"							</Properties>" +
"						</HorizontalLayout>" +
"						<HorizontalLayout Id=\"ct001_ct005_ct003\">" +
"							<Properties>" +
"								<Width Value=\"30%\" />" +
"								<Height Value=\"100%\" />" +
"							</Properties>" +
"							<Components>" +
"								<Button Id=\"ct001_ct005_ct003_ct001\">" +
"									<Properties>" +
"										<Caption Value=\"Běž na další &gt;\" />" +
"									</Properties>" +
"									<Handlers>" +
"										<Click Action=\"finishSlide\" />" +
"									</Handlers>" +
"								</Button>" +
"							</Components>" +
"						</HorizontalLayout>" +
"					</Components>" +
"				</HorizontalLayout>" +
"			</Components>" +
"		</VerticalLayout>" +
"		<Handlers>" +
"			<Init>" +
"				<Action/>" +
"			</Init>" +
"			<Show>" +
"				<Action/>" +
"			</Show>" +
"		</Handlers>" +
"	</Viewport>" +
"	<Windows>" +
"		<Window Id=\"ct002\">" +
"			<Properties>" +
"				<Width Value=\"960\" />" +
"				<Height Value=\"690\" />" +
"				<SolidMask Value=\"False\" />" +
"			</Properties>" +
"			<Components>" +
"				<HorizontalLayout Id=\"ct002_ct001\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"100%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<Image Id=\"ct002_ct001_ct001\" />" +
"					</Components>" +
"				</HorizontalLayout>" +
"			</Components>" +
"			<Handlers>" +
"				<Init>" +
"					<Action/>" +
"				</Init>" +
"				<Show>" +
"					<Action/>" +
"				</Show>" +
"				<Close>" +
"					<Action/>" +
"				</Close>" +
"			</Handlers>" +
"		</Window>" +
"	</Windows>" +
"	<Variables>" +
"		<Variable Id=\"pocitadlo\" Type=\"Integer\" Value=\"0\" />" +
"		<Variable Id=\"tlacitko\" Type=\"Object\">" +
"			<Reference>" +
"				<Component Id=\"ct001_ct005_ct001_ct001\" />" +
"			</Reference>" +
"		</Variable>" +
"	</Variables>" +
"	<Actions>" +
"		<Action Id=\"finishSlide\">" +
"			<Command>Finish</Command>" +
"		</Action>" +
"		<Action Id=\"prictipocitadlo\">" +
"			<Expression>pocitadlo=pocitadlo+1</Expression>" +
"			<If>" +
"				<Expression>pocitadlo==5</Expression>" +
"				<True>" +
"					<Call Action=\"finishSlide\" />" +
"				</True>" +
"			</If>" +
"		</Action>" +
"	</Actions>" +
"	<OutputValue>" +
"		<Expression>pocitadlo</Expression>" +
"	</OutputValue>" +
"</SlideTemplate>";

	public static final String CONTENT_XML =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideContent TemplateUID=\"B80BF9AC-C488-41F7-AB95-52BB7EAB9F50\">" +
"  <Bindings>" +
"    <Bind>" +
"      <Panel Id=\"ct001_ct001_ct001\">" +
"        <Properties>" +
"          <Border Value=\"False\" />" +
"        </Properties>" +
"      </Panel>" +
"    </Bind>" +
"    <Bind>" +
"      <Panel Id=\"ct001_ct003_ct001\">" +
"        <Properties>" +
"          <Height Value=\"100%\" />" +
"        </Properties>" +
"        <Components>" +
"          <Label Id=\"ct001_ct003_ct001_ct001\">" +
"            <Properties>" +
"              <Caption Value=\"Nudný text v pravém dolním rohu panelu\" />" +
"              <Alignment Value=\"br\" />" +
"            </Properties>" +
"          </Label>" +
"        </Components>" +
"      </Panel>" +
"    </Bind>" +
"    <Bind>" +
"      <Button Id=\"ct001_ct005_ct001_ct001\">" +
"        <Properties>" +
"          <Caption Value=\"Tajná akce\" />" +
"        </Properties>" +
"      </Button>" +
"    </Bind>" +
"  </Bindings>" +
"</SlideContent>";

	public static final String TEMPLATE_XML1 = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideTemplate UID=\"507E8D72-6EBC-11E1-A494-17324924019B\">" +
"	<Viewport>" +
"		<VerticalLayout Id=\"ct001\">" +
"			<Properties>" +
"				<Width Value=\"100%\" />" +
"				<Height Value=\"100%\" />" +
"			</Properties>" +
"			<Components>" +
"				<HorizontalLayout Id=\"ct001_ct001\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"80%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<Panel Id=\"ct001_ct001_ct001\">" +
"							<Properties>" +
"								<Border Value=\"True\" />" +
"							</Properties>" +
"						</Panel>" +
"					</Components>" +
"				</HorizontalLayout>" +
"				<HorizontalLayout Id=\"ct001_ct002\">" +
"					<Properties>" +
"						<Width Value=\"100%\" />" +
"						<Height Value=\"20%\" />" +
"						<Alignment Value=\"mc\" />" +
"					</Properties>" +
"					<Components>" +
"						<Button Id=\"ct001_ct002_ct001\">" +
"							<Properties>" +
"								<Caption Value=\"Konec\" />" +
"							</Properties>" +
"							<Handlers>" +
"								<Click>" +
"									<Action>" +
"										<Command>Finish</Command>" +
"									</Action>" +
"								</Click>" +
"							</Handlers>" +
"						</Button>" +
"					</Components>" +
"				</HorizontalLayout>" +
"			</Components>" +
"		</VerticalLayout>" +
"	</Viewport>" +
"</SlideTemplate>";

	public static final String CONTENT_XML1 =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideContent TemplateUID=\"507E8D72-6EBC-11E1-A494-17324924019B\">" +
"  <Bindings>" +
"    <Bind>" +
"      <Panel Id=\"ct001_ct001_ct001\">" +
"        <Properties>" +
"          <Width Value=\"50%\" />" +
"          <Height Value=\"100%\" />" +
"        </Properties>" +
"        <Components>" +
"          <Label Id=\"ct001_ct001_ct001_ct001\">" +
"            <Properties>" +
"              <Caption Value=\"Varianta 1\" />" +
"            </Properties>" +
"          </Label>" +
"        </Components>" +
"      </Panel>" +
"    </Bind>" +
"  </Bindings>" +
"</SlideContent>";

	public static final String CONTENT_XML2 =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideContent TemplateUID=\"507E8D72-6EBC-11E1-A494-17324924019B\">" +
"  <Bindings>" +
"    <Bind>" +
"      <Panel Id=\"ct001_ct001_ct001\">" +
"        <Properties>" +
"          <Width Value=\"100%\" />" +
"          <Height Value=\"50%\" />" +
"        </Properties>" +
"        <Components>" +
"          <Label Id=\"ct001_ct001_ct001_ct001\">" +
"            <Properties>" +
"              <Caption Value=\"Varianta 2\" />" +
"            </Properties>" +
"          </Label>" +
"        </Components>" +
"      </Panel>" +
"    </Bind>" +
"  </Bindings>" +
"</SlideContent>";

	public static final String CONTENT_XML3 =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
"<SlideContent TemplateUID=\"507E8D72-6EBC-11E1-A494-17324924019B\">" +
"  <Bindings>" +
"    <Bind>" +
"      <Panel Id=\"ct001_ct001_ct001\">" +
"        <Properties>" +
"          <Width Value=\"50%\" />" +
"          <Height Value=\"50%\" />" +
"        </Properties>" +
"        <Components>" +
"          <Label Id=\"ct001_ct001_ct001_ct001\">" +
"            <Properties>" +
"              <Caption Value=\"Varianta 3\" />" +
"            </Properties>" +
"          </Label>" +
"        </Components>" +
"      </Panel>" +
"    </Bind>" +
"  </Bindings>" +
"</SlideContent>";

}
